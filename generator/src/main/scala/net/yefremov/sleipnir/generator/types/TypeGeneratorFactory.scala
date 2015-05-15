/*
 *    Copyright 2015 Dmitriy Yefremov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.yefremov.sleipnir.generator.types

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
