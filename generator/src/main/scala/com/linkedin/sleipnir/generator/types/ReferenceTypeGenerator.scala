package com.linkedin.sleipnir.generator.types


import com.linkedin.data.schema.TyperefDataSchema
import com.linkedin.data.DataMap

import com.linkedin.sleipnir.generator.GeneratedClass

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._


/**
 * A generator for [[TyperefDataSchema]] types. It does nothing except providing type meta information in the generators chain.
 * @author Dmitriy Yefremov
 */
class ReferenceTypeGenerator(override val schema: TyperefDataSchema,
                             override val parentGenerator: Option[TypeGenerator],
                             override val namespacePrefix: Option[String],
                             override val filename: String) extends AbstractTypeGenerator {

  // for predefined types we can not overwrite the name, so we always delegate the name decision to the referenced generator
  // for typerefs with a custom java class binding, we take the name of that class
  override def name: TypeName = customType.getOrElse(referencedGenerator.name)

  private def referencedGenerator = nestedGenerator(schema.getRef)

  override def referencedGenerators: Seq[TypeGenerator] = Seq(referencedGenerator)

  override val generateClass: Option[GeneratedClass] = None

  private def javaProperties: Option[Map[String, Any]] = {
    val properties = schema.getProperties.asScala
    properties.get("java").map {
      case map: DataMap => map.toMap
      case _ => throw new IllegalArgumentException(schema + """has "java" property that is not a DataMap""")
    }
  }

  /**
   * Returns the custom binding type if it is defined in the schema.
   */
  def customType: Option[TypeName] = {
    javaProperties.flatMap { map =>
      map.get("class").map {
        case customClass: String => TypeName(customClass)
        case _ => throw new IllegalArgumentException(schema + """has "java" property with "class" that is not a string""")
      }
    }
  }

  /**
   * Returns the custom coercer type if it is defined in the schema.
   */
  def customCoercerType: Option[TypeName] = {
    javaProperties.flatMap { map =>
      map.get("coercerClass").map {
        case coercerClass: String => TypeName(coercerClass)
        case _ => throw new IllegalArgumentException(schema + """has "java" property with "coercerClass" that is not a string""")
      }
    }
  }
}
