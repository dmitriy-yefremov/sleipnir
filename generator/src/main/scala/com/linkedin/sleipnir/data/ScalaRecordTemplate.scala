package com.linkedin.sleipnir.data

import java.util.{Map => JMap}
import java.util.{Collection => JCollection}

import scala.reflect.runtime.universe._

import scala.collection.JavaConverters._

import com.linkedin.data.{DataList, DataMap}
import com.linkedin.data.schema.RecordDataSchema
import com.linkedin.data.template._
import com.linkedin.sleipnir.generator.types.{ArrayTypeGenerator, MapTypeGenerator, TypeGeneratorFactory}


/**
 * An abstract class that is extended by all Scala record template classes. This class is designed to take care of all heavy
 * lifting and make extending classes really lightweight. It in turn simplifies the logic to generate those classes.
 * @author Dmitriy Yefremov
 */
abstract class ScalaRecordTemplate(data: DataMap, schema: RecordDataSchema) extends RecordTemplate(data, schema) {

  private val mirror: Mirror = runtimeMirror(getClass.getClassLoader)

  private def runtimeClass[T](tpe: Type): Class[T] = {
    mirror.runtimeClass(tpe).asInstanceOf[Class[T]]
  }

  private def runtimeClass[T](name: String): Class[T] = {
    Class.forName(name).asInstanceOf[Class[T]]
  }

  /**
   * A universal setter for any type of field.
   * It takes care of different behavior for different data types as well as of Scala to Java conversion.
   * @param field the field to set
   * @param value the value to set to the field
   * @tparam T the field type
   */
  protected def set[T: TypeTag](field: RecordDataSchema.Field, value: T): Unit = {
    typeOf[T] match {
      case dataType if dataType <:< typeOf[DataTemplate[_]] =>
        setDataTemplate(field, value.asInstanceOf[DataTemplate[_]])
      case optionType if optionType <:< typeOf[Option[_]] =>
        setOption(field, value.asInstanceOf[Option[_]])
      case listType if listType <:< typeOf[Seq[Any]] =>
        setArray(field, value.asInstanceOf[Seq[Any]])
      case mapType if mapType <:< typeOf[Map[String,Any]] =>
        setMap(field, value.asInstanceOf[Map[String, Any]])
      case enumType if enumType <:< typeOf[Enumeration#Value] =>
        setEnum(field, value.asInstanceOf[Enumeration#Value])
      case directType if field.getType.isPrimitive =>
        setPrimitive(field, value)
    }
  }

  private def setPrimitive[T](field: RecordDataSchema.Field, value: T): Unit = {
    val clazz = value.getClass.asInstanceOf[Class[Any]]
    putDirect(field, clazz, value)
  }

  private def setOption[T](field: RecordDataSchema.Field, option: Option[T]): Unit = {
    //TODO add support for complex types
    option.foreach { v =>
      val clazz = v.getClass.asInstanceOf[Class[Any]]
      putDirect(field, clazz, v)
    }
  }

  private def setDataTemplate[T](field: RecordDataSchema.Field, data: DataTemplate[T]): Unit = {
    val clazz = data.getClass.asInstanceOf[Class[DataTemplate[T]]]
    putWrapped(field, clazz, data)
  }

  private def setMap[T](field: RecordDataSchema.Field, map: Map[String, T]): Unit = {
    val generator = TypeGeneratorFactory.instance(field.getType).asInstanceOf[MapTypeGenerator]
    val wrapperClass = runtimeClass[DataTemplate[DataMap]](generator.fullClassName)
    val wrapper = if (generator.schema.getValues.isComplex) {
      val wrapperConstructor = wrapperClass.getConstructor(classOf[Map[String,Any]])
      wrapperConstructor.newInstance(map)
    } else {
      val wrapperConstructor = wrapperClass.getConstructor(classOf[JMap[String,Any]])
      wrapperConstructor.newInstance(map.asJava)
    }
    putWrapped(field, wrapperClass, wrapper)
  }

  private def setArray[T](field: RecordDataSchema.Field, array: Seq[T]): Unit = {
    val generator = TypeGeneratorFactory.instance(field.getType).asInstanceOf[ArrayTypeGenerator]
    val wrapperClass = runtimeClass[DataTemplate[DataList]](generator.fullClassName)
    val wrapper = if (generator.schema.getItems.isComplex) {
      val wrapperConstructor = wrapperClass.getConstructor(classOf[Seq[Any]])
      wrapperConstructor.newInstance(array)
    } else {
      val wrapperConstructor = wrapperClass.getConstructor(classOf[JCollection[Any]])
      wrapperConstructor.newInstance(array.asJavaCollection)
    }
    putWrapped(field, wrapperClass, wrapper)
  }

  private def setEnum[T](field: RecordDataSchema.Field, value: Enumeration#Value): Unit = {
    val name = value.toString
    putDirect(field, classOf[String], name)
  }

  /**
   * A universal getter for any type of field.
   * It takes care of different behavior for different data types as well as of Java to Scala conversion.
   * @param field the field to get
   * @tparam T the field type
   * @return the value stored in the underlying data map converted to an appropriate Scala type
   */
  protected def get[T: TypeTag](field: RecordDataSchema.Field): T = {
    typeOf[T] match {
      case dataType if dataType <:< typeOf[DataTemplate[_]] =>
        getDataTemplate[T](field)
      case optionType if optionType <:< typeOf[Option[_]] =>
        getOption[T](field)
      case listType if listType <:< typeOf[Seq[Any]] =>
        getArray[T](field)
      case mapType if mapType <:< typeOf[Map[String,Any]] =>
        getMap[T](field)
      case enumType if enumType <:< typeOf[Enumeration#Value] =>
        getEnum[T](field)
      case directType if field.getType.isPrimitive =>
        getDirect[T](field)
    }
  }

  private def getDirect[T: TypeTag](field: RecordDataSchema.Field): T = {
    obtainDirect(field, runtimeClass[Any](typeOf[T]), GetMode.STRICT).asInstanceOf[T]
  }

  private def getOption[T : TypeTag](field: RecordDataSchema.Field): T = {
    //TODO add support for complex types
    Option(obtainDirect(field, runtimeClass[Any](typeOf[T]), GetMode.NULL)).asInstanceOf[T]
  }

  private def getDataTemplate[T : TypeTag](field: RecordDataSchema.Field): T = {
    obtainWrapped(field, runtimeClass[DataTemplate[Any]](typeOf[T]), GetMode.STRICT).asInstanceOf[T]
  }

  private def getMap[T : TypeTag](field: RecordDataSchema.Field): T = {
    val generator = TypeGeneratorFactory.instance(field.getType).asInstanceOf[MapTypeGenerator]
    val map = if (generator.schema.getValues.isComplex) {
      val wrapperClass = runtimeClass[ScalaMapTemplate](generator.fullClassName)
      obtainWrapped(field, wrapperClass, GetMode.STRICT).map
    } else {
      val wrapperClass = runtimeClass[DirectMapTemplate[Any]](generator.fullClassName)
      obtainWrapped(field, wrapperClass, GetMode.STRICT).asScala.toMap
    }
    map.asInstanceOf[T]
  }

  private def getArray[T : TypeTag](field: RecordDataSchema.Field): T = {
    val generator = TypeGeneratorFactory.instance(field.getType).asInstanceOf[ArrayTypeGenerator]
    val result = if (generator.schema.getItems.isComplex) {
      val wrapperClass = runtimeClass[ScalaArrayTemplate](generator.fullClassName)
      obtainWrapped(field, wrapperClass, GetMode.STRICT).items
    } else {
      val wrapperClass = runtimeClass[DirectArrayTemplate[Any]](generator.fullClassName)
      obtainWrapped(field, wrapperClass, GetMode.STRICT).asScala.toSeq
    }
    result.asInstanceOf[T]
  }

  private def getEnum[T : TypeTag](field: RecordDataSchema.Field): T = {
    val preType = typeOf[T] match {
      case TypeRef(pre, _, _) => pre
    }
    val name = obtainDirect(field, classOf[String], GetMode.STRICT)
    val methodSymbol = preType.member(newTermName("withName")).asMethod
    val moduleSymbol = preType.termSymbol.asModule
    val moduleMirror = mirror.reflectModule(moduleSymbol)
    val instanceMirror = mirror.reflect(moduleMirror.instance)
    val result = instanceMirror.reflectMethod(methodSymbol)(name)
    result.asInstanceOf[T]
  }

}
