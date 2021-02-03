package generator

data class GeneratorConfig(
    val builderClassSuffix: String = "Builder",
    val withFunctionPrefix: String = "with",
    val withoutFunctionPrefix: String = "without",
    val buildFunctionName: String = "build",
                          )
