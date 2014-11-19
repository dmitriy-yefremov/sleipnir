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
   * @param parentSchema schema defining the parent type
   */
  def instance(schema: DataSchema, parentSchema: DataSchema): TypeGenerator = instance(schema, Some(parentSchema))

  private def instance(schema: DataSchema, parentSchemaOpt: Option[DataSchema]): TypeGenerator = {
    schema match {
      case primitive: PrimitiveDataSchema => PrimitiveTypeGenerator(primitive)
      case enum: EnumDataSchema => EnumTypeGenerator(enum)
      case record: RecordDataSchema => RecordTypeGenerator(record)
      case fixed: FixedDataSchema => FixedTypeGenerator(fixed)
      case array: ArrayDataSchema if array.getItems.isComplex => ComplexArrayTypeGenerator(array)
      case array: ArrayDataSchema if array.getItems.isPrimitive => PrimitiveArrayTypeGenerator(array)
      case map: MapDataSchema if map.getValues.isComplex => ComplexMapTypeGenerator(map)
      case map: MapDataSchema if map.getValues.isPrimitive => PrimitiveMapTypeGenerator(map)
      case ref: TyperefDataSchema => RefTypeGenerator(ref)
      case union: UnionDataSchema => parentSchemaOpt match {
        case Some(record: RecordDataSchema) => UnionTypeGenerator(union, record)
      }
    }
  }

}
