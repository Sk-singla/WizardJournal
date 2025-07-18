package com.samapps.wizardjournal.audio.record

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}