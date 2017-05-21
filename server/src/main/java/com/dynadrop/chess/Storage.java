package com.dynadrop.chess;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileWriter;
import java.io.File;
import org.apache.log4j.Logger;

public class Storage {
    private static final String relativeFolder = "games/";
    private static final Logger logger = Logger.getLogger(GameHandler.class);

    public static Object get(String fileName) {
      System.out.println("Storage.get("+relativeFolder + fileName+")");
      Object obj = null;
      try{
        FileInputStream fis = new FileInputStream(relativeFolder + fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        obj = ois.readObject();
        ois.close();
        return obj;
      } catch (IOException e) {
        logger.info("someone tried to find not existing game "+fileName);
      } catch (ClassNotFoundException e) {
        logger.error("exception", e);
      }
      return obj;
    }

    public static void put(Object obj, String fileName) {
      try{
        File file = new File(relativeFolder + fileName);
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        FileOutputStream fos = new FileOutputStream(relativeFolder + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
      } catch (IOException e) {
        logger.error("exception", e);
      }
    }

}
