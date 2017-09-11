package com.xfinity.rmvp_compiler;

import javax.lang.model.element.TypeElement;

class UnnamedPackageException extends Exception {

  public UnnamedPackageException(TypeElement typeElement) {
    super("The package of " + typeElement.getSimpleName() + " is unnamed");
  }
}
