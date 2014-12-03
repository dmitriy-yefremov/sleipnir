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
   */
  def instance(schema: DataSchema): TypeGenerator = instance(schema, None)

  /**
   * Returns an instance of [[TypeGenerator]] for the given type.
   * @param schema schema defining the type
   * @param parentGenerator generator for the parent type
   */
  def instance(schema: DataSchema, parentGenerator: TypeGenerator): TypeGenerator = instance(schema, Some(parentGenerator))

  private def instance(schema: DataSchema, parentGeneratorOpt: Option[TypeGenerator]): TypeGenerator = {
    schema.getDereferencedDataSchema match {
      case bytes: BytesDataSchema =>
        new BytesTypeGenerator(bytes, parentGeneratorOpt)
      case primitive: PrimitiveDataSchema =>
        new PrimitiveTypeGenerator(primitive, parentGeneratorOpt)
      case enum: EnumDataSchema =>
        new EnumTypeGenerator(enum, parentGeneratorOpt)
      case record: RecordDataSchema =>
        new RecordTypeGenerator(record, parentGeneratorOpt)
      case fixed: FixedDataSchema =>
        new FixedTypeGenerator(fixed, parentGeneratorOpt)
      case array: ArrayDataSchema if array.getItems.getDereferencedDataSchema.isComplex =>
        new ComplexArrayTypeGenerator(array, parentGeneratorOpt)
      case array: ArrayDataSchema if array.getItems.getDereferencedDataSchema.isPrimitive =>
        new PrimitiveArrayTypeGenerator(array, parentGeneratorOpt)
      case map: MapDataSchema if map.getValues.getDereferencedDataSchema.isComplex =>
        new ComplexMapTypeGenerator(map, parentGeneratorOpt)
      case map: MapDataSchema if map.getValues.getDereferencedDataSchema.isPrimitive =>
        new PrimitiveMapTypeGenerator(map, parentGeneratorOpt)
      case union: UnionDataSchema =>
        new UnionTypeGenerator(union, parentGeneratorOpt)
    }
  }

}
