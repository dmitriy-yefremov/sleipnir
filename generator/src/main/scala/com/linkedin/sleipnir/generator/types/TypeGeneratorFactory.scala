package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema._

/**
 * Creates instances of [[TypeGenerator]] for different [[DataSchema]]s.
 * @author Dmitriy Yefremov
 */
object TypeGeneratorFactory {

  /**
   * Returns an instance of [[TypeGenerator]] for the given type. This method should be called when the parent schema is
   * unknown / does not exist.
   * @param schema schema defining the type
   * @param namespacePrefix optional prefix that is added to name space of the generated types
   */
  def instance(schema: DataSchema, namespacePrefix: Option[String]): TypeGenerator = {
    instance(schema, None, namespacePrefix)
  }

  /**
   * Returns an instance of [[TypeGenerator]] for the given type.
   * @param schema schema defining the type
   * @param parentGenerator generator for the parent type
   * @param namespacePrefix optional prefix that is added to name space of the generated types
   */
  def instance(schema: DataSchema, parentGenerator: TypeGenerator, namespacePrefix: Option[String]): TypeGenerator = {
    instance(schema, Some(parentGenerator), namespacePrefix)
  }

  private def instance(schema: DataSchema, parentGeneratorOpt: Option[TypeGenerator], namespacePrefix: Option[String]): TypeGenerator = {
    schema.getDereferencedDataSchema match {
      case primitive: PrimitiveDataSchema =>
        new PrimitiveTypeGenerator(primitive, parentGeneratorOpt, namespacePrefix)
      case enum: EnumDataSchema =>
        new EnumTypeGenerator(enum, parentGeneratorOpt, namespacePrefix)
      case record: RecordDataSchema =>
        new RecordTypeGenerator(record, parentGeneratorOpt, namespacePrefix)
      case fixed: FixedDataSchema =>
        new FixedTypeGenerator(fixed, parentGeneratorOpt, namespacePrefix)
      case array: ArrayDataSchema if array.getItems.getDereferencedDataSchema.isComplex =>
        new ComplexArrayTypeGenerator(array, parentGeneratorOpt, namespacePrefix)
      case array: ArrayDataSchema if array.getItems.getDereferencedDataSchema.isPrimitive =>
        new PrimitiveArrayTypeGenerator(array, parentGeneratorOpt, namespacePrefix)
      case map: MapDataSchema if map.getValues.getDereferencedDataSchema.isComplex =>
        new ComplexMapTypeGenerator(map, parentGeneratorOpt, namespacePrefix)
      case map: MapDataSchema if map.getValues.getDereferencedDataSchema.isPrimitive =>
        new PrimitiveMapTypeGenerator(map, parentGeneratorOpt, namespacePrefix)
      case union: UnionDataSchema =>
        new UnionTypeGenerator(union, parentGeneratorOpt, namespacePrefix)
    }
  }

}
