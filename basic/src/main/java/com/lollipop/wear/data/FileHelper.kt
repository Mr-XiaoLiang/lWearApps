package com.lollipop.wear.data

import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

object FileHelper {

    fun write(file: File, data: JSONObject): FileResult<File> {
        return tryWrapper {
            write(file, data.toString())
        }
    }

    fun readJson(file: File): FileResult<JSONObject> {
        return readString(file).mapResult { JSONObject(it) }
    }

    fun write(file: File, data: String): FileResult<File> {
        return tryWrapper {
            write(file, data.toByteArray())
        }
    }

    fun readString(file: File): FileResult<String> {
        return readByteArray(file).mapResult { String(it) }
    }

    fun write(file: File, data: ByteArray): FileResult<File> {
        return tryDo {
            val input = ByteArrayInputStream(data)
            val output = file.outputStream()
            file.parentFile?.mkdirs()
            if (file.exists()) {
                file.delete()
            }
            input.use {
                output.use {
                    input.copyTo(output)
                    output.flush()
                }
            }
            file
        }
    }

    fun readByteArray(file: File): FileResult<ByteArray> {
        return tryDo {
            val input = file.inputStream()
            val output = ByteArrayOutputStream()
            input.use {
                output.use {
                    input.copyTo(output)
                    output.flush()
                }
            }
            output.toByteArray()
        }
    }

    private inline fun <reified I, reified O> FileResult<I>.mapResult(mapper: (I) -> O): FileResult<O> {
        return try {
            when (this) {
                is FileResult.Failure -> {
                    FileResult.Failure(error)
                }

                is FileResult.Success -> {
                    FileResult.Success(mapper(data))
                }
            }
        } catch (e: Throwable) {
            FileResult.Failure(e)
        }
    }

    private inline fun <reified O> tryWrapper(block: () -> FileResult<O>): FileResult<O> {
        return try {
            block()
        } catch (e: Throwable) {
            FileResult.Failure(e)
        }
    }

    private inline fun <reified O> tryDo(block: () -> O): FileResult<O> {
        return try {
            FileResult.Success(block())
        } catch (e: Throwable) {
            FileResult.Failure(e)
        }
    }

    sealed class FileResult<T> {
        class Success<T>(val data: T) : FileResult<T>()
        class Failure<T>(val error: Throwable) : FileResult<T>()
    }

}