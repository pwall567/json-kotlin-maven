/*
 * @(#) JSONSchemaCodegenMojo.kt
 *
 * json-kotlin-maven  Maven Code Generation Plugin for JSON Schema
 * Copyright (c) 2021, 2023 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.json.kotlin.codegen.maven

import java.io.File

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo

import net.pwall.json.pointer.JSONPointer
import net.pwall.json.schema.codegen.CodeGenerator
import net.pwall.json.schema.codegen.TargetFileName
import net.pwall.json.schema.codegen.TargetLanguage

@Mojo(name = "codegen", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
class JSONSchemaCodegenMojo : AbstractMojo() {

    @Parameter
    var configFile: File? = null

    @Parameter
    var inputFile: File? = null

    @Parameter
    var packageName: String? = null

    @Parameter
    var language: TargetLanguage? = null

    @Parameter
    var outputDir: File? = null

    @Parameter(property = "generatorComment")
    var comment: String? = null

    @Parameter
    var pointer: String? = null

    @Parameter
    var indexFileName: String? = null

    override fun execute() {
        CodeGenerator().apply {
            val parser = schemaParser
            val config = configFile ?: File("src/main/resources/codegen-config.json").takeIf { it.exists() }
            config?.let { configure(it) }
            packageName?.let { basePackageName = it }
            language?.let { targetLanguage = it }
            val output = outputDir ?: File("target/generated-sources/${targetLanguage.directory()}")
            baseDirectoryName = output.path
            comment?.let { generatorComment = it }
            this@JSONSchemaCodegenMojo.indexFileName?.let {
                indexFileName = if (it.contains('.'))
                    TargetFileName(it.substringBefore('.'), it.substringAfter('.'))
                else
                    TargetFileName(it)
            }
            val input = inputFile ?: File("src/main/resources/schema")
            pointer?.let {
                generateAll(parser.jsonReader.readJSON(input), JSONPointer(it))
            } ?: generate(input)
        }
    }

    private fun TargetLanguage.directory() = when (this) {
        TargetLanguage.KOTLIN -> "kotlin"
        TargetLanguage.JAVA,
        TargetLanguage.JAVA16 -> "java"
        TargetLanguage.TYPESCRIPT -> "ts"
        TargetLanguage.MARKDOWN -> "doc"
    }

}
