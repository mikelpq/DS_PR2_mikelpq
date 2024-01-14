package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.traversal.Iterator;

import java.time.LocalDate;

public class Employee {

    private final String employeeId;
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String role;
    private final LinkedList<Room> rooms = new LinkedList<>();

    public Employee(String employeeId, String name, String surname, LocalDate localDate, String role) {
        this.employeeId = employeeId;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = localDate;
        this.role = role;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRole(){
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LinkedList<Room> getRooms() {
        return rooms;
    }

    public void update(String name, String surname, LocalDate date, String role) {
        setName(name);
        setSurname(surname);
        setDateOfBirth(date);
        setRole(role);
    }

    public void addRoom(Room room){
        this.rooms.insertEnd(room);
    }

    public boolean isAssignedToRoom(Room room) {
        Iterator<Room> iterator = this.rooms.values();
        boolean isAssigned = false;

        while (iterator.hasNext()) {
            if (iterator.next().getRoomId().equals(room.getRoomId())) {
                isAssigned = true;
                break;
            }
        }

        return isAssigned;
    }

    public boolean isNewRole(String role) {
        return !role.equals(this.role);
    }
}
