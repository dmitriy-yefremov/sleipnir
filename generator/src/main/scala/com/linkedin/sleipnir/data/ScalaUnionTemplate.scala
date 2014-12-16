package com.linkedin.sleipnir.data

import com.linkedin.data.schema._
import com.linkedin.data.template.UnionTemplate

/**
 * A super type for all union types. This class is designed to take care of all heavy lifting and make extending classes
 * really lightweight. It in turn simplifies the logic to generate those classes.
 * @author Dmitriy Yefremov
 */
abstract class ScalaUnionTemplate protected(data: AnyRef, schema: UnionDataSchema) extends UnionTemplate(data, schema) {

}
