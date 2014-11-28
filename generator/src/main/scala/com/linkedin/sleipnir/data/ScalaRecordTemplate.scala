package com.linkedin.sleipnir.data

import java.util.{Collection => JCollection, Map => JMap}

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

import com.linkedin.data.{DataList, DataMap}
import com.linkedin.data.schema._
import com.linkedin.data.template._
import com.linkedin.sleipnir.generator.types._

/**
 * An abstract class that is extended by all Scala record template classes. This class is designed to take care of all heavy
 * lifting and make extending classes really lightweight. It in turn simplifies the logic to generate those classes.
 * @author Dmitriy Yefremov
 */
abstract class ScalaRecordTemplate(data: DataMap, schema: RecordDataSchema) extends RecordTemplate(data, schema) {

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
  protected def set[T](field: RecordDataSchema.Field, value: T): Unit = {
    value match {
      case data: DataTemplate[_] =>
        setDataTemplate(field, value.asInstanceOf[DataTemplate[_]])
      case list: Seq[Any] =>
        setArray(field, list)
      case map: Map[String, Any] =>
        setMap(field, map)
      case enum: Enumeration#Value =>
        setEnum(field, enum)
      case directType if field.getType.isPrimitive =>
        setPrimitive(field, value)
    }
  }

  /**
   * A universal setter for optional fields. It does nothing if the given value is [[None]], otherwise delegates to [[set()]].
   */
  protected def setOptional[T](field: RecordDataSchema.Field, valueOption: Option[T]): Unit = {
    valueOption.foreach { value =>
      set(field, value)
    }
  }

  private def setPrimitive[T](field: RecordDataSchema.Field, value: T): Unit = {
    val clazz = value.getClass.asInstanceOf[Class[Any]]
    putDirect(field, clazz, value)
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
  protected def get[T: ClassTag](field: RecordDataSchema.Field): T = {
    field.getType.getDereferencedDataSchema match {
      case primitive: PrimitiveDataSchema => getDirect[T](field)
      case enum: EnumDataSchema => getEnum[T](field)
      case record: RecordDataSchema => getDataTemplate[T](field)
      case fixed: FixedDataSchema => getDataTemplate[T](field)
      case array: ArrayDataSchema => getArray[T](field)
      case map: MapDataSchema => getMap[T](field)
      case union: UnionDataSchema => getDataTemplate[T](field)
    }
  }

  /**
   * A universal getter for optional fields. It returns [[None]] if the field does not exist, otherwise delegates to [[get()]].
   */
  protected def getOptional[T: ClassTag](field: RecordDataSchema.Field): Option[T] = {
    if (contains(field)) {
      val value = get[T](field)
      Option(value)
    } else {
      None
    }
  }

  private def getDirect[T: ClassTag](field: RecordDataSchema.Field): T = {
    val clazz = implicitly[ClassTag[T]].runtimeClass
    obtainDirect(field, clazz, GetMode.STRICT).asInstanceOf[T]
  }

  private def getDataTemplate[T : ClassTag](field: RecordDataSchema.Field): T = {
    val clazz = implicitly[ClassTag[T]].runtimeClass
    obtainWrapped(field, clazz.asInstanceOf[Class[DataTemplate[_]]], GetMode.STRICT).asInstanceOf[T]
  }

  private def getMap[T : ClassTag](field: RecordDataSchema.Field): T = {
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

  private def getArray[T : ClassTag](field: RecordDataSchema.Field): T = {
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

  private def getEnum[T : ClassTag](field: RecordDataSchema.Field): T = {
    /*val generator = TypeGeneratorFactory.instance(field.getType).asInstanceOf[EnumTypeGenerator]
    generator.fullClassName
    val preType = typeOf[T] match {
      case TypeRef(pre, _, _) => pre
    }
    val name = obtainDirect(field, classOf[String], GetMode.STRICT)
    val methodSymbol = preType.member(newTermName("withName")).asMethod
    val moduleSymbol = preType.termSymbol.asModule
    val moduleMirror = mirror.reflectModule(moduleSymbol)
    val instanceMirror = mirror.reflect(moduleMirror.instance)
    val result = instanceMirror.reflectMethod(methodSymbol)(name)
    result.asInstanceOf[T]*/
    ???
  }

}
