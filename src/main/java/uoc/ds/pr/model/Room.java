package uoc.ds.pr.model;

import uoc.ds.pr.CTTCompaniesJobsPR2;

public class Room {
    private String roomId;
    private String name;
    private String description;
    private int numEquipments;
    private CTTCompaniesJobsPR2.RoomType roomType;

    public Room(String roomId, String name, String description, CTTCompaniesJobsPR2.RoomType roomType) {
        this.roomId = roomId;
        this.name = name;
        this.description = description;
        this.roomType = roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int numEquipments() {
        return numEquipments;
    }

    public void setNumEquipments(int numEquipments) {
        this.numEquipments = numEquipments;
    }
}
