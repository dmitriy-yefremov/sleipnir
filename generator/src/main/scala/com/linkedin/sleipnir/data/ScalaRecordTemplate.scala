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
    set(field, value, typeOf[T])
  }

  private def set[T](field: RecordDataSchema.Field, value: T, valueType: Type): Unit = {
    valueType match {
      case dataType if dataType <:< typeOf[DataTemplate[_]] =>
        setDataTemplate(field, value.asInstanceOf[DataTemplate[_]])
      case optionType if optionType <:< typeOf[Option[_]] =>
        setOption(field, value.asInstanceOf[Option[_]], optionType)
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

  private def setOption[T](field: RecordDataSchema.Field, option: Option[T], optionType: Type): Unit = {
    option.foreach { v =>
      optionType match {
        case optionType @ TypeRef(_, _, typeArg::Nil) => set(field, v, typeArg)
      }
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
    get(field, typeOf[T])
  }

  private def get[T](field: RecordDataSchema.Field, valueType: Type): T = {
    valueType match {
      case dataType if dataType <:< typeOf[DataTemplate[_]] =>
        getDataTemplate[T](field, valueType)
      case optionType if optionType <:< typeOf[Option[_]] =>
        getOption[T](field, valueType)
      case listType if listType <:< typeOf[Seq[Any]] =>
        getArray[T](field, valueType)
      case mapType if mapType <:< typeOf[Map[String,Any]] =>
        getMap[T](field, valueType)
      case enumType if enumType <:< typeOf[Enumeration#Value] =>
        getEnum[T](field, valueType)
      case directType if field.getType.isPrimitive =>
        getDirect[T](field, valueType)
    }
  }

  private def getDirect[T](field: RecordDataSchema.Field, valueType: Type): T = {
    obtainDirect(field, runtimeClass[Any](valueType), GetMode.STRICT).asInstanceOf[T]
  }

  private def getOption[T](field: RecordDataSchema.Field, valueType: Type): T = {
    val result = if (contains(field)) {
      valueType match {
        case optionType @ TypeRef(_, _, typeArg::Nil) => Some(get(field, typeArg))
      }
    } else {
      None
    }
    result.asInstanceOf[T]
  }

  private def getDataTemplate[T](field: RecordDataSchema.Field, valueType: Type): T = {
    obtainWrapped(field, runtimeClass[DataTemplate[Any]](valueType), GetMode.STRICT).asInstanceOf[T]
  }

  private def getMap[T](field: RecordDataSchema.Field, valueType: Type): T = {
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

  private def getArray[T](field: RecordDataSchema.Field, valueType: Type): T = {
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

  private def getEnum[T](field: RecordDataSchema.Field, valueType: Type): T = {
    val preType = valueType match {
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
