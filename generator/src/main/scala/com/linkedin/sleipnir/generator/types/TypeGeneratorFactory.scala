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
  def instance(schema: DataSchema, namespacePrefix: Option[String], filename: String): TypeGenerator = {
    instance(schema, None, namespacePrefix, filename)
  }

  /**
   * Returns an instance of [[TypeGenerator]] for the given type.
   * @param schema schema defining the type
   * @param parentGenerator generator for the parent type
   * @param namespacePrefix optional prefix that is added to name space of the generated types
   */
  def instance(schema: DataSchema, parentGenerator: TypeGenerator, namespacePrefix: Option[String],
               filename: String): TypeGenerator = {
    instance(schema, Some(parentGenerator), namespacePrefix, filename)
  }

  private def instance(schema: DataSchema, parentGeneratorOpt: Option[TypeGenerator], namespacePrefix: Option[String],
                       filename: String): TypeGenerator = {
    schema match {
      case typeref: TyperefDataSchema =>
        new ReferenceTypeGenerator(typeref, parentGeneratorOpt, namespacePrefix, filename)
      case primitive: PrimitiveDataSchema =>
        new PrimitiveTypeGenerator(primitive, parentGeneratorOpt, namespacePrefix, filename)
      case enum: EnumDataSchema =>
        new EnumTypeGenerator(enum, parentGeneratorOpt, namespacePrefix, filename)
      case record: RecordDataSchema =>
        new RecordTypeGenerator(record, parentGeneratorOpt, namespacePrefix, filename)
      case fixed: FixedDataSchema =>
        new FixedTypeGenerator(fixed, parentGeneratorOpt, namespacePrefix, filename)
      case array: ArrayDataSchema if array.getItems.isComplex =>
        new ComplexArrayTypeGenerator(array, parentGeneratorOpt, namespacePrefix, filename)
      case array: ArrayDataSchema if array.getItems.isPrimitive =>
        new PrimitiveArrayTypeGenerator(array, parentGeneratorOpt, namespacePrefix, filename)
      case map: MapDataSchema if map.getValues.isComplex =>
        new ComplexMapTypeGenerator(map, parentGeneratorOpt, namespacePrefix, filename)
      case map: MapDataSchema if map.getValues.isPrimitive =>
        new PrimitiveMapTypeGenerator(map, parentGeneratorOpt, namespacePrefix, filename)
      case union: UnionDataSchema =>
        new UnionTypeGenerator(union, parentGeneratorOpt, namespacePrefix, filename)
    }
  }
}
