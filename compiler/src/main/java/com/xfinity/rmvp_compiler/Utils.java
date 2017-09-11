package com.xfinity.rmvp_compiler;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

final class Utils {

  private Utils() {
    // no instances
  }

  static String getPackageName(Elements elementUtils, TypeElement type)
      throws UnnamedPackageException {
    PackageElement pkg = elementUtils.getPackageOf(type);
    if (pkg.isUnnamed()) {
      throw new UnnamedPackageException(type);
    }
    return pkg.getQualifiedName().toString();
  }
}
