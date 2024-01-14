package uoc.ds.pr.model;

import java.util.Comparator;

public class Equipment {

    public static final Comparator<Equipment> CMP_E = Comparator.comparing(Equipment::getEquipmentId);
    private final String equipmentId;
    private String name;
    private String description;
    private Room room;

    public Equipment(String equipmentId, String name, String description) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.description = description;
    }

    public Equipment(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentId() {
        return equipmentId;
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

    public void update(String name, String description) {
        setName(name);
        setDescription(description);
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public boolean isInRoom(String roomId) {
        return this.room != null && this.room.getRoomId().equals(roomId);
    }
}
