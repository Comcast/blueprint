@file:JvmName("Utils")

package com.xfinity.blueprint_compiler

import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

@Throws(UnnamedPackageException::class)
fun  Elements.getPackageName(type: TypeElement): String {
  val pkg = this.getPackageOf(type)

  if (pkg.isUnnamed) {
      throw UnnamedPackageException(type)
  }

  return pkg.qualifiedName.toString()
}
