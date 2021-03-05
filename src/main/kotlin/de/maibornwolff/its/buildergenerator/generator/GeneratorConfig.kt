package de.maibornwolff.its.buildergenerator.generator

data class GeneratorConfig(
    var builderClassSuffix: String = "Builder",
    var withFunctionPrefix: String = "with",
    var withoutFunctionPrefix: String = "without",
    var buildFunctionName: String = "build",
                          )
