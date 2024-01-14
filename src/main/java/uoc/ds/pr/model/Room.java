package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.CTTCompaniesJobsPR2;

import java.util.Comparator;

public class Room implements Comparable<Room>{
    public static final Comparator<Room> CMP_R = Comparator.comparing(Room::numEquipments);;
    private String roomId;
    private String name;
    private String description;
    private CTTCompaniesJobsPR2.RoomType roomType;
    private LinkedList<Employee> assignedEmployees;
    private LinkedList<Equipment> equipments = new LinkedList<>();

    public Room(String roomId, String name, String description, CTTCompaniesJobsPR2.RoomType roomType) {
        this.roomId = roomId;
        this.name = name;
        this.description = description;
        this.roomType = roomType;
        this.assignedEmployees = new LinkedList<>();
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

    public void setRoomType(CTTCompaniesJobsPR2.RoomType roomType) {
        this.roomType = roomType;
    }

    public LinkedList<Employee> getAssignedEmployees() {
        return assignedEmployees;
    }

    public LinkedList<Equipment> getEquipments() {
        return equipments;
    }

    public int numEquipments(){
        return equipments.size();
    }

    public void update(String name, String description, CTTCompaniesJobsPR2.RoomType roomtype) {
        setName(name);
        setDescription(description);
        setRoomType(roomtype);
    }

    public void addEmployee(Employee employee) {
        this.assignedEmployees.insertEnd(employee);
    }

    public void addEquipment(Equipment equipment){
        this.equipments.insertEnd(equipment);
    }

    public void removeEquipment (String equipment) {
        LinkedList<Equipment> newList = new LinkedList<>();
        Iterator<Equipment> iterator = this.equipments.values();

        while (iterator.hasNext()) {
            Equipment cur = iterator.next();
            boolean isSameEquipment = cur.getEquipmentId().equals(equipment);
            if (!isSameEquipment) {
                newList.insertEnd(cur);
            }
        }

        this.equipments = newList;
    }

    public boolean hasEquipment(String equipment) {
        boolean found = false;
        Iterator<Equipment> iterator = this.equipments.values();

        while (iterator.hasNext()) {
            Equipment cur = iterator.next();
            boolean isSameEquipment = cur.getEquipmentId().equals(equipment);
            if (!isSameEquipment) {
                found = true;
                break;
            }
        }
        return found;
    }

    @Override
    public int compareTo(Room o) {
        return this.roomId.compareTo(o.roomId);
    }
}
