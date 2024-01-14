package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.traversal.Iterator;

public class Role {
    private final String roleId;
    private String description;
    private LinkedList<Employee> employees;

    public Role(String roleId, String description) {
        this.roleId = roleId;
        this.description = description;
        this.employees = new LinkedList<>();
    }

    public String getRoleId() {
        return roleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LinkedList<Employee> getEmployees() {
        return employees;
    }

    public void update(String description) {
        this.description = description;
    }

    public void addEmployee(Employee employee) {
        this.employees.insertEnd(employee);
    }

    public void removeEmployee(String employee) {
        LinkedList<Employee> newList = new LinkedList<>();
        Iterator<Employee> iterator = this.employees.values();

        while (iterator.hasNext()) {
            Employee cur = iterator.next();
            boolean isSameEmployee = cur.getEmployeeId().equals(employee);
            if (!isSameEmployee) {
                newList.insertEnd(cur);
            }
        }

        this.employees = newList;
    }

    public boolean isEmployeeAlreadyInRole(String employee) {
        Iterator<Employee> iterator = this.employees.values();
        boolean found = false;

        while (iterator.hasNext()) {
            Employee cur = iterator.next();
            boolean isSameEmployee = cur.getEmployeeId().equals(employee);
            if (isSameEmployee) {
                found = true;
                break;
            }
        }
        return found;
    }
}
