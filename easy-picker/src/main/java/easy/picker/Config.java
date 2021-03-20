package easy.picker;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import easy.fp.EasyFP;

public class Config {

    public static Config defaultConfig(Context context) {
        File file = new File(context.getCacheDir(), ".easypicker");
        if (!file.exists()) {
            file.mkdirs();
        }
        return new Builder().copyDir(file).noMedia()
                .outputFactory(new OutputFactory() {
                    @Override
                    public EasyFP.Pair createOutput(Context context) {
                        return EasyFP.withDefault(context).availableCacheDir()
                                .name(UUID.randomUUID().toString())
                                .pair();
                    }
                }).build();
    }

    private final File copyDir;
    private OutputFactory outputFactory;

    private Config(File copyDir, boolean noMedia, OutputFactory factory) {
        this.copyDir = copyDir;
        this.outputFactory = factory;
        if (noMedia) {
            File noMediaFile = new File(copyDir, ".nomedia");
            if (noMediaFile.exists()) {
                return;
            }
            try {
                noMediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getCopyDir() {
        return copyDir;
    }

    public OutputFactory getOutputFactory() {
        return outputFactory;
    }

    public static class Builder {

        private File copyDir;
        private boolean noMedia;
        private OutputFactory outputFactory;

        public Builder copyDir(File copyDir) {
            this.copyDir = copyDir;
            return this;
        }

        public Builder noMedia() {
            noMedia = true;
            return this;
        }

        public Builder outputFactory(OutputFactory factory) {
            this.outputFactory = factory;
            return this;
        }

        public Config build() {
            if (copyDir == null) {
                throw new IllegalArgumentException("copyDir not configured");
            }
            if (outputFactory == null) {
                throw new IllegalArgumentException("outputFactory not configured");
            }
            return new Config(copyDir, noMedia, outputFactory);
        }
    }

    public interface OutputFactory {
        EasyFP.Pair createOutput(Context context);
    }

}