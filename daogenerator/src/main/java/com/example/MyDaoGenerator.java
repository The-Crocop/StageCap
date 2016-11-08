package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(7, "icap.vv.de.subtitlepresenter.pojo");
        schema.setDefaultJavaPackageDao("icap.vv.de.subtitlepresenter.pojo");
        schema.setDefaultJavaPackageTest("icap.vv.de.subtitlepresenter.test");
        schema.enableKeepSectionsByDefault();

        Entity subtitleProject = schema.addEntity("SubtitleProject");
       // subtitleProject.addIdProperty().autoincrement();
        subtitleProject.addStringProperty("title").unique().primaryKey().notNull();
        subtitleProject.addIntProperty("position");
        subtitleProject.implementsSerializable();


        Entity infinotedProject = schema.addEntity("InfinotedProject");
        infinotedProject.addIdProperty().primaryKey().autoincrement();
        infinotedProject.addStringProperty("url");
        infinotedProject.addStringProperty("docPath");
        infinotedProject.addStringProperty("srtFile");
        infinotedProject.implementsSerializable();

        Property subtitleProjectId = infinotedProject.addStringProperty("subtitleProjectTitle").notNull().getProperty();
        infinotedProject.addToOne(subtitleProject,subtitleProjectId);
        subtitleProject.addToMany(infinotedProject,subtitleProjectId);

       // Property infinotedId = infinotedProject.addStringProperty("id").primaryKey().getProperty();





        //Property subtitleProjects = infinotedProject.addStringProperty("subtitleProject_id").notNull().getProperty();

      //  infinotedProject.addToOne(subtitleProject,subtitleProjects);
      //  subtitleProject.addToMany(infinotedProject, subtitleProjects);


        new DaoGenerator().generateAll(schema,"../app/src/main/java");
    }
}


