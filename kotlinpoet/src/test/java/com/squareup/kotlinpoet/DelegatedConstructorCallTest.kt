package com.squareup.kotlinpoet

import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class DelegatedConstructorCallTest {
  @Test
  fun defaultPresentInNonExternalClass() {
    val builder = TypeSpec.classBuilder("Test")
      .superclass(ClassName("testpackage", "TestSuper"))
    assertThat(builder.build().toString()).isEqualTo(
      """
        |public class Test : testpackage.TestSuper()
        |""".trimMargin()
    )
  }

  @Test
  fun defaultPresentInNonExternalObject() {
    val builder = TypeSpec.objectBuilder("Test")
      .superclass(ClassName("testpackage", "TestSuper"))
    assertThat(builder.build().toString()).isEqualTo(
      """
        |public object Test : testpackage.TestSuper()
        |""".trimMargin()
    )
  }

  @Test
  fun defaultNotPresentInExternalClass() {
    val builder = TypeSpec.classBuilder("Test")
      .addModifiers(KModifier.EXTERNAL)
      .superclass(ClassName("testpackage", "TestSuper"))
    assertThat(builder.build().toString()).isEqualTo(
      """
        |public external class Test : testpackage.TestSuper
        |""".trimMargin()
    )
  }

  @Test
  fun defaultNotPresentInExternalObject() {
    val builder = TypeSpec.objectBuilder("Test")
      .addModifiers(KModifier.EXTERNAL)
      .superclass(ClassName("testpackage", "TestSuper"))
    assertThat(builder.build().toString()).isEqualTo(
      """
        |public external object Test : testpackage.TestSuper
        |""".trimMargin()
    )
  }

  @Test
  fun allowedInNonExternalClass() {
    val builder = TypeSpec.classBuilder("Test")
      .superclass(ClassName("testpackage", "TestSuper"))
      .addSuperclassConstructorParameter("anything")
    assertThat(builder.build().toString()).isEqualTo(
      """
        |public class Test : testpackage.TestSuper(anything)
        |""".trimMargin()
    )
  }

  @Test
  fun allowedInNonExternalObject() {
    val builder = TypeSpec.objectBuilder("Test")
      .superclass(ClassName("testpackage", "TestSuper"))
      .addSuperclassConstructorParameter("anything")
    assertThat(builder.build().toString()).isEqualTo(
      """
        |public object Test : testpackage.TestSuper(anything)
        |""".trimMargin()
    )
  }

  @Test
  fun allowedInNonExternalClassSecondary() {
    val builder = TypeSpec.classBuilder("Test")
    val primaryConstructorBuilder = FunSpec.constructorBuilder()
    val primaryConstructor = primaryConstructorBuilder.build()
    builder.primaryConstructor(primaryConstructor)
    val secondaryConstructorBuilder = FunSpec.constructorBuilder()
      .addParameter(ParameterSpec("foo", ClassName("kotlin", "String")))
      .callThisConstructor()
    builder.addFunction(secondaryConstructorBuilder.build())
    assertThat(builder.build().toString()).isEqualTo(
      """
        |public class Test {
        |  public constructor(foo: kotlin.String) : this()
        |}
        |""".trimMargin()
    )
  }

  @Test
  fun notAllowedInExternalClass() {
    val builder = TypeSpec.classBuilder("Test")
      .addModifiers(KModifier.EXTERNAL)
      .superclass(ClassName("testpackage", "TestSuper"))
      .addSuperclassConstructorParameter("anything")
    assertThrows<IllegalStateException> {
      builder.build()
    }
  }

  @Test
  fun notAllowedInExternalObject() {
    val builder = TypeSpec.objectBuilder("Test")
      .addModifiers(KModifier.EXTERNAL)
      .superclass(ClassName("testpackage", "TestSuper"))
      .addSuperclassConstructorParameter("anything")
    assertThrows<IllegalStateException> {
      builder.build()
    }
  }

  @Test
  fun notAllowedInExternalClassSecondary() {
    val builder = TypeSpec.classBuilder("Test")
      .addModifiers(KModifier.EXTERNAL)
    val primaryConstructorBuilder = FunSpec.constructorBuilder()
    builder.primaryConstructor(primaryConstructorBuilder.build())
    val secondaryConstructorBuilder = FunSpec.constructorBuilder()
      .addParameter(ParameterSpec("foo", ClassName("kotlin", "String")))
      .callThisConstructor()
    builder.addFunction(secondaryConstructorBuilder.build())
    assertThrows<IllegalStateException> {
      builder.build()
    }
  }
}
