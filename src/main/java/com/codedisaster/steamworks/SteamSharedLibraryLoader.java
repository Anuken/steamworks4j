package com.codedisaster.steamworks;

import java.io.*;
import java.util.*;

class SteamSharedLibraryLoader{
    private static final PLATFORM OS;
    private static final boolean IS_64_BIT;

    static{
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");

        if(osName.contains("Windows")){
            OS = PLATFORM.Windows;
        }else if(osName.contains("Linux")){
            OS = PLATFORM.Linux;
        }else if(osName.contains("Mac")){
            OS = PLATFORM.MacOS;
        }else{
            throw new RuntimeException("Unknown host architecture: " + osName + ", " + osArch);
        }

        IS_64_BIT = osArch.equals("amd64") || osArch.equals("x86_64");
    }

    private static String getPlatformLibName(String libName, boolean use64){
        switch(OS){
            case Windows:
                return libName + (IS_64_BIT && use64 ? "64" : "") + ".dll";
            case Linux:
                return "lib" + libName + ".so";
            case MacOS:
                return "lib" + libName + ".dylib";
        }

        throw new RuntimeException("Unknown host architecture");
    }

    static void loadLibrary(String... libraryNames) throws SteamException{
        Throwable firstException = null;
        for(File file : extractLocations("steamworks4j_" + Steamworks4j.version, "out")){
            if(canWrite(file)){
                try{
                    //try to extract and load each file
                    loadAllLibraries(file, true, libraryNames);
                    return;
                }catch(Throwable t){
                    firstException = t;
                    //'non-valid win32 application' error
                    if(t.getMessage() != null && t.getMessage().contains("Win32")){
                        //try to load them again, but this time in 32-bit mode
                        try{
                            loadAllLibraries(file, false, libraryNames);
                            return;
                        }catch(Throwable ignored){
                            //didn't work, keep trying
                        }
                    }
                    //TODO handle 32-bit errors.
                }
            }
        }

        if(firstException != null){
            throw new SteamException(firstException);
        }
    }

    private static void loadAllLibraries(File file, boolean use64, String... libraryNames) throws Throwable{
        for(String lib : libraryNames){
            String libFilename = getPlatformLibName(lib, use64);
            File libFile = new File(file.getParentFile(), libFilename);
            extractLibrary(libFile, libFilename);
            System.load(libFile.getCanonicalPath());
        }
    }

    private static File[] extractLocations(String folderName, String fileName){
        ArrayList<File> out = new ArrayList<>();

        out.add(new File(System.getProperty("java.io.tmpdir") + "/" + folderName, fileName));

        try{
            File file = File.createTempFile(folderName, null);
            if(file.delete()){
                out.add(new File(file, fileName));
            }
        }catch(IOException ignored){}

        out.add(new File(System.getProperty("user.home") + "/." + folderName, fileName));
        out.add(new File(".tmp/" + folderName, fileName));
        out.add(new File(fileName));

        return out.toArray(new File[0]);
    }

    private static void extractLibrary(File destination, String fileName) throws IOException{
        InputStream input = SteamSharedLibraryLoader.class.getResourceAsStream("/" + fileName);

        if(input != null){
            try(FileOutputStream output = new FileOutputStream(destination)){
                byte[] buffer = new byte[4096];
                while(true){
                    int length = input.read(buffer);
                    if(length == -1) break;
                    output.write(buffer, 0, length);
                }
            }catch(IOException e){
                //Extracting the library may fail, for example because 'nativeFile' already exists and is inuse by another process.
                //In this case, we fail silently and just try to load the existing file.
                if(!destination.exists()){
                    throw e;
                }
            }finally{
                input.close();
            }
        }else{
            throw new IOException("Failed to read input stream for " + destination.getCanonicalPath());
        }
    }

    private static File discoverExtractLocation(String folderName, String fileName) throws IOException{
        File[] paths = extractLocations(folderName, fileName);
        for(File file : paths){
            if(canWrite(file)){
                return file;
            }
        }

        throw new IOException("No suitable extraction path found");
    }

    private static boolean canWrite(File file){

        File folder = file.getParentFile();

        if(file.exists()){
            if(!file.canWrite() || !canExecute(file)){
                return false;
            }
        }else{
            if(!folder.exists()){
                if(!folder.mkdirs()){
                    return false;
                }
            }
            if(!folder.isDirectory()){
                return false;
            }
        }

        File testFile = new File(folder, UUID.randomUUID().toString());

        try{
            new FileOutputStream(testFile).close();
            return canExecute(testFile);
        }catch(IOException e){
            return false;
        }finally{
            testFile.delete();
        }
    }

    private static boolean canExecute(File file){
        try{
            if(file.canExecute()){
                return true;
            }

            if(file.setExecutable(true)){
                return file.canExecute();
            }
        }catch(Exception ignored){

        }

        return false;
    }

    enum PLATFORM{
        Windows,
        Linux,
        MacOS
    }

}
