package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.TyperefDataSchema
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.data.DataMap
import scala.collection.JavaConverters._

/**
 * A generator for [[TyperefDataSchema]] types. It does nothing except providing type meta information in the generators chain.
 * @author Dmitriy Yefremov
 */
class ReferenceTypeGenerator(override val schema: TyperefDataSchema,
                             override val parentGenerator: Option[TypeGenerator],
                             override val namespacePrefix: Option[String]) extends AbstractTypeGenerator {

  // for predefined types we can not overwrite the name, so we always delegate the name decision to the referenced generator
  // for typerefs with a custom java class binding, we take the name of that class
  override def name: TypeName = customType.getOrElse(referencedGenerator.name)

  private def referencedGenerator = nestedGenerator(schema.getRef)

  override def referencedGenerators: Seq[TypeGenerator] = Seq(referencedGenerator)

  override val generateClass: Option[GeneratedClass] = None

  /**
   * Returns the custom binding type if it is defined in the schema.
   */
  def customType: Option[TypeName] = {
    val properties = schema.getProperties.asScala
    properties.get("java").flatMap {
      case map: DataMap =>
        map.asScala.get("class").map {
          case customClass: String => TypeName(customClass)
          case _ => throw new IllegalArgumentException(schema + """has "java" property with "class" that is not a string""")
        }
      case _ => throw new IllegalArgumentException(schema + """has "java" property that is not a DataMap""")
    }
  }

}
