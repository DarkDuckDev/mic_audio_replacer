package com.example.mic_recorder;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.TargetDataLine;
import java.io.File;

@Slf4j
public class MicRecorderApplication {

    private static final Long RECORD_TIME = 5_000L;
    private static final AudioFileFormat.Type FORMAT = AudioFileFormat.Type.WAVE;
    private volatile TargetDataLine targetDataLine;

    private static AudioFormat getAudioFormat() {
        return new AudioFormat(16000, 8, 1, true, true);
    }

    @SneakyThrows
    public static void main(String[] args) {

        MicRecorderApplication recorder = new MicRecorderApplication();
        new Thread(() -> {
            try {
                Thread.sleep(RECORD_TIME);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
            recorder.stopCapturing();

        }).start();
        recorder.startCapturing();
    }

    @SneakyThrows
    private void startCapturing() {
        File targetFile = filePreparation();

        AudioFormat audioFormat = getAudioFormat();
        Info info = new Info(TargetDataLine.class, audioFormat);
        if (!AudioSystem.isLineSupported(info)) {
            System.exit(0);
        }

        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        targetDataLine.open(audioFormat);
        targetDataLine.start();
        log.info("Start listening");

        final AudioInputStream ais = new AudioInputStream(targetDataLine);
        AudioSystem.write(ais, FORMAT, targetFile);
    }

    private void stopCapturing() {
        log.info("Stop capturing");
        targetDataLine.stop();
        targetDataLine.close();
    }

    @SneakyThrows
    private File filePreparation() {
        File targetFile = new File("sample.wav");
        if (targetFile.exists()) {
            targetFile.delete();
        }
        targetFile.createNewFile();
        log.info("Created file");

        return targetFile;
    }

}
