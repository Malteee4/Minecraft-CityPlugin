package de.malteee.citysystem.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    private Connection con = null;

    public Database connect(String filename) {
        try {
            Class.forName("org.sqlite.JDBC");
            String connectionUrl ="jdbc:sqlite:" + filename;
            con = DriverManager.getConnection(connectionUrl);
            System.out.println("Sqlite connected!");
            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_properties(CODE varchar(20), VALUE varchar(60))").execute();
            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_players(PLAYER_ID varchar(40), MONEY int, JOB varchar(20), RANK varchar(20), HOME varchar(40))").execute();
            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_jobs(PLAYER_ID varchar(40), LUMBERJACK_EXP varchar(40), FISHER_EXP varchar(40), HUNTER_EXP varchar(40), BUILDER_EXP varchar(40), " +
                    "MINER_EXP varchar(40), TRADER_EXP varchar(40))").execute();    //(exp,money)

            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_player_stats(PLAYER_ID varchar(40), BLOCK_BREAK int, BLOCK_PLACE int, " +
                    "ENTITY_KILL int, PLAYER_KILL int, DEATHS int, DISTANCE int)").execute();

            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_areas(AREA_ID varchar(20), TYPE varchar(20), LOC1 varchar(30), LOC2 varchar(30), SUPERIOR varchar(30))").execute();
            //con.prepareStatement("DROP TABLE tbl_superior_areas").execute();
            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_superior_areas(AREA_ID varchar(20), LOC1 varchar(30), LOC2 varchar(30))").execute();

            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_residential(RESIDENTIAL_ID varchar(30), BUILDING_RIGHTS varchar(200), OWNER varchar(30))").execute();
            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_residential_areas(RESIDENTIAL_ID varchar(30), AREA_ID varchar(30))").execute();
            //con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_shop(SHOP_ID varchar(30), )").execute();
            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_shop_areas(SHOP_ID varchar(30), AREA_ID varchar(30))").execute();
            //con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_farm()").execute();

            //con.prepareStatement("DROP TABLE tbl_city").execute();
            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_city(CITY_ID varchar(30), WELCOME_MSG varchar(100), GOODBYE_MSG varchar(100), SPAWN varchar(30), "
              + "PLAYER_ID varchar(40), DAYS_ACTIVE int, PUBLIC_SPAWN bool, BUILD_RIGHT varchar(300), EXPANSION varchar(50), STAGE varchar(20))").execute();

            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_city_areas(AREA_ID varchar(30), CITY_ID varchar(30))").execute();
            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_city_residential_plots(RESIDENTIAL_ID varchar(30), CITY_ID varchar(30))").execute();
            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_city_shops(SHOP_ID varchar(30), CITY_ID varchar(30))").execute();

            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_portal(LOC1 varchar(40), LOC2 varchar(40), DESTINATION varchar(40))").execute();

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }return this;
    }
    public void disconnect() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void execute(String stmt) {
        try {
            con.prepareStatement(stmt).execute();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getResult(String stmt) {
        try {
            return con.prepareStatement(stmt).executeQuery();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Connection getCon() {
        return con;
    }
}
