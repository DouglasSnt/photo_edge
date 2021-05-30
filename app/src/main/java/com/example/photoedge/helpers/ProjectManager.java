package com.example.photoedge.helpers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.photoedge.gson.ExclusionStrategy;
import com.example.photoedge.models.LayerModel;
import com.example.photoedge.models.ProjectModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ProjectManager {

    private static final String TAG = "ProjectManager";
    private static final String PROJECT_FOLDER = "projects";
    private static final String LAYER_FOLDER = "layers";
    private static final String PROJECT_FILE = "proj_file";
    private static final String THUMBNAIL_FILE = "thumbnail";
    private static final String EXPORTED_FOLDER = "Photo Edge";

    public static boolean createProject(Context context, ProjectModel projectModel) {
        File filesDir = context.getFilesDir();
        File projectFolder = new File(filesDir.getPath() + "/" + PROJECT_FOLDER + "/" + projectModel.getProjectName());

        if (!projectFolder.exists()) {
            if (!projectFolder.mkdirs()) {
                Log.d(TAG, "Can't create project folder");
                return false;
            }
        }

        File layersFolder = new File(projectFolder.getPath() + "/" + LAYER_FOLDER);
        if (!layersFolder.exists()) {
            if (!layersFolder.mkdirs()) {
                Log.d(TAG, "Can't create layers folder");
                return false;
            }
        }
        LayerModel Layer = projectModel.getLayerList().get(0);

        File baseLayer = new File(layersFolder.getPath() + "/" + Layer.getLayerName());
        File thumbFile = new File(projectFolder.getPath() + "/" + THUMBNAIL_FILE);

        if (!baseLayer.exists()) {
            try {
                if (!baseLayer.createNewFile()) {
                    Log.d(TAG, "Can't create base layer");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try (FileOutputStream out = new FileOutputStream(thumbFile)) {
            Layer.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream out = new FileOutputStream(baseLayer)) {
            Layer.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        File configs = new File(projectFolder + "/" + PROJECT_FILE);
        if (!configs.exists()) {
            try {
                if (!configs.createNewFile()) {
                    Log.d(TAG, "Can't create configs file");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        writeToFile(configs, projectModel);

        return true;
    }

    public static void deleteProject(Context context, ProjectModel projectModel) {
        File filesDir = context.getFilesDir();
        File projectFolder = new File(filesDir.getPath() + "/" + PROJECT_FOLDER + "/" + projectModel.getProjectName());

        deleteRecursive(projectFolder);
    }

    public static void deleteRecursive(File file) {
        if (file.isDirectory())
            for (File child : file.listFiles())
                deleteRecursive(child);

        file.delete();
    }

    public static void saveProject(Context context, ProjectModel projectModel) {
        File filesDir = context.getFilesDir();
        File projectFolder = new File(filesDir.getPath() + "/" + PROJECT_FOLDER + "/" + projectModel.getProjectName());
        File layersFolder = new File(projectFolder.getPath() + "/" + LAYER_FOLDER);

        if (!layersFolder.exists()) {
            if (!layersFolder.mkdirs()) {
                Log.d(TAG, "Can't create layers folder");
            }
        }

        String[] children = layersFolder.list();
        for (String child : children) {
            new File(layersFolder, child).delete();
        }

        List<LayerModel> layerList = projectModel.getLayerList();
        Bitmap thumbBm = Bitmap.createBitmap(projectModel.getWidth(), projectModel.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvasThumb = new Canvas(thumbBm);

        Paint opacityPaint = new Paint();
        for (int i = layerList.size(); i > 0; i--) {
            if (!layerList.get(i - 1).getVisibility()) continue;
            Bitmap bm = layerList.get(i - 1).getBitmap();
            opacityPaint.setAlpha((int) (255 * (layerList.get(i - 1).getOpacity() / 100f)));
            canvasThumb.drawBitmap(bm, 0, 0, opacityPaint);
        }

        for (LayerModel layerModel : layerList) {
            try {
                File layer = new File(layersFolder.getPath() + "/" + layerModel.getLayerName());
                if (!layer.exists()) {
                    if (!layer.createNewFile()) {
                        Log.d(TAG, "Can't create layer");
                    }
                }

                FileOutputStream out = new FileOutputStream(layer);
                out.flush();
                layerModel.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            File thumbFile = new File(projectFolder.getPath() + "/" + THUMBNAIL_FILE);
            FileOutputStream out = new FileOutputStream(thumbFile);
            out.flush();
            thumbBm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File configs = new File(projectFolder + "/" + PROJECT_FILE);
        if (!configs.exists()) {
            try {
                if (!configs.createNewFile()) {
                    Log.d(TAG, "Can't create configs file");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeToFile(configs, projectModel);
    }

    public static void exportImage(Context context, ProjectModel projectModel) {

        String filename = System.currentTimeMillis() + ".png";
        OutputStream fos = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver cr = context.getContentResolver();
            ContentValues cv = new ContentValues();

            cv.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            cv.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            cv.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + EXPORTED_FOLDER);

            Uri imageUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

            try {
                fos = cr.openOutputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = new File(imagesDir, filename);
            try {
                fos = new FileOutputStream(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        List<LayerModel> layerList = projectModel.getLayerList();
        Bitmap image = Bitmap.createBitmap(projectModel.getWidth(), projectModel.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvasImage = new Canvas(image);

        Paint opacityPaint = new Paint();
        for (int i = layerList.size(); i > 0; i--) {
            if (!layerList.get(i - 1).getVisibility()) continue;
            Bitmap bm = layerList.get(i - 1).getBitmap();
            opacityPaint.setAlpha((int) (255 * (layerList.get(i - 1).getOpacity() / 100f)));
            canvasImage.drawBitmap(bm, 0, 0, opacityPaint);
        }

        image.compress(Bitmap.CompressFormat.PNG, 100, fos);
    }


    public static Bitmap getThumbnail(Context context, String projectName) {
        File filesDir = context.getFilesDir();
        File projectFolder = new File(filesDir.getPath() + "/" + PROJECT_FOLDER + "/" + projectName);

        try {
            return BitmapFactory.decodeFile(projectFolder.getPath() + "/" + THUMBNAIL_FILE);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<ProjectModel> loadAllProjects(Context context, boolean importBitmap) {
        File filesDir = context.getFilesDir();
        File projectFolder = new File(filesDir.getPath() + "/" + PROJECT_FOLDER);

        File[] projectFiles = projectFolder.listFiles();
        List<ProjectModel> projectNames = new ArrayList<>();
        if (projectFiles != null) {
            for (File projectFile : projectFiles) {
                projectNames.add(loadProjectByName(context, projectFile.getName(), importBitmap));
            }
        }
        return projectNames;
    }


    public static void writeToFile(File file, Object obj) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy()).create();
            writer.write(gson.toJson(obj));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ProjectModel loadProjectByName(Context context, String projectName, boolean importBitmap) {
        try {
            File filesDir = context.getFilesDir();
            File projectFolder = new File(filesDir.getPath() + "/" + PROJECT_FOLDER + "/" + projectName);
            File projFile = new File(projectFolder + "/" + PROJECT_FILE);

            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(projFile));
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String projJson = sb.toString();

            ProjectModel projectModel = new GsonBuilder().create().fromJson(projJson, ProjectModel.class);

            if (importBitmap) {
                List<LayerModel> layerModels = projectModel.getLayerList();

                for (LayerModel layerModel : layerModels) {
                    String layerPath = projectFolder + "/" + LAYER_FOLDER + "/" + layerModel.getLayerName();

                    if (!new File(layerPath).exists()) continue;

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;

                    Bitmap bm = BitmapFactory.decodeFile(layerPath, options);
                    layerModel.setBitmap(bm);
                }

                for (int i = 0; i < layerModels.size(); i++) {
                    if (layerModels.get(i).getBitmap() == null) {
                        projectModel.deleteLayer(i);
                    }
                }
            }

            return projectModel;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
