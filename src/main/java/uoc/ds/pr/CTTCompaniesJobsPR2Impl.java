package uoc.ds.pr;

import edu.uoc.ds.adt.helpers.KeyValue;
import edu.uoc.ds.adt.nonlinear.AVLTree;
import edu.uoc.ds.adt.nonlinear.Dictionary;
import edu.uoc.ds.adt.nonlinear.DictionaryAVLImpl;
import edu.uoc.ds.adt.nonlinear.HashTable;
import edu.uoc.ds.adt.nonlinear.graphs.*;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.Queue;
import edu.uoc.ds.adt.sequential.SetLinkedListImpl;
import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.model.*;
import uoc.ds.pr.util.*;

import java.time.LocalDate;

public class CTTCompaniesJobsPR2Impl implements CTTCompaniesJobsPR2 {
    private final AVLTree<Worker> workers = new AVLTree<>(Worker.CMP_W);
    private final AVLTree<JobOffer> jobOffers = new AVLTree<>(JobOffer.CMP_J);
    //private final Dictionary<String,JobOffer> jobOffers = new DictionaryAVLImpl<>(JobOffer.CMP_KV);
    private final AVLTree<Equipment> equipments = new AVLTree<>(Equipment.CMP_E);
    private final DSArray<Role> roles = new DSArray<>(MAX_NUM_ROLES);
    private final HashTable<String, Room> rooms = new HashTable<>();
    private final HashTable<String, Employee> employees = new HashTable<>();
    private final HashTable<String, Company> companies = new HashTable<>();
    private final Queue<Request> requests = new QueueLinkedList<>();
    private int totalRequests = 0;
    private int rejectedRequests = 0;
    private Worker mostActiveWorker;
    private final OrderedVector<JobOffer> bestJobOffer = new OrderedVector<>(MAX_NUM_JOBOFFERS, JobOffer.CMP_V);
    private final OrderedVector<Room> mostEquippedRooms = new OrderedVector<>(MAX_BEST5_EQUIPPEMENT, Room.CMP_R);
    private LinkedList<Room> roomsWithoutEmployees = new LinkedList<>();
    private final DirectedGraph<Employee, String> employeesNetworK = new DirectedGraphImpl<>();

    @Override
    public void addWorker(String id, String name, String surname, LocalDate dateOfBirth, Qualification qualification) {
        Worker foundWorker = getWorker(id);

        if (foundWorker != null) {
            foundWorker.update(name, surname, dateOfBirth, qualification);
        } else {
            Worker worker = new Worker(id, name, surname, dateOfBirth, qualification);
            this.workers.add(worker);
        }
    }

    @Override
    public void addCompany(String id, String name, String description) {
        Company company = getCompany(id);

        if (company != null) {
            company.update(name, description);
        }
        this.companies.put(id, new Company(id, name, description));
    }

    @Override
    public void addRequest(String id, String jobOfferId, String companyId, String description, Qualification minQualification, int maxWorkers, LocalDate startDate, LocalDate endDate) throws CompanyNotFoundException {
        Company foundCompany = getCompany(companyId);

        if (foundCompany == null) {
            throw new CompanyNotFoundException();
        }

        requests.add(new Request(id, jobOfferId, foundCompany, description, minQualification, maxWorkers, startDate, endDate));
        this.totalRequests++;
    }

    @Override
    public Request updateRequest(Status status, LocalDate date, String description) throws NoRequestException {
        if (requests.isEmpty()) throw new NoRequestException();
        Request request = requests.poll();

        if (request  == null) {
            throw new NoRequestException();
        }

        request.update(status, date, description);

        if (request.isEnabled()) {
            JobOffer jobOffer = request.getJobOffer();

            this.jobOffers.add(jobOffer);
            Company company = jobOffer.getCompany();
            company.addJobOffer(jobOffer);

        }
        else {
            this.rejectedRequests++;
        }
        return request;
    }

    @Override
    public Response signUpJobOffer(String workerId, String jobOfferId) throws JobOfferNotFoundException, WorkerNotFoundException, WorkerAlreadyEnrolledException {
        Response response = Response.REJECTED;
        Worker worker = getWorker(workerId);
        JobOffer jobOffer = getJobOffer(jobOfferId);
        if (worker == null) {
            throw new WorkerNotFoundException();
        }
        if (jobOffer == null) {
            throw new JobOfferNotFoundException();
        }

        if (worker.isInJobOffer(jobOffer)) {
            throw new WorkerAlreadyEnrolledException();
        }

        if (worker.isInJobOfferAsSubstitute(jobOffer)) {
            throw new WorkerAlreadyEnrolledException();
        }

        if (jobOffer.workerHasMinimumQualification(worker)){
            if (!jobOffer.isfull()) {
                response = Response.ACCEPTED;
                jobOffer.addRegistration(worker, response);
                worker.addJobOffer(jobOffer);
                updateMostActiveWorker(worker);
            }
            else {
                response = Response.SUBSTITUTE;
                jobOffer.addRegistration(worker, response);
            }
        }

        return response;
    }

    private void updateMostActiveWorker(Worker worker) {
        if (this.mostActiveWorker == null || this.mostActiveWorker.getWorkingDays() < worker.getWorkingDays()) {
            this.mostActiveWorker = worker;
        }
    }

    @Override
    public double getPercentageRejectedRequests() {
        return (double) this.rejectedRequests / this.totalRequests;
    }

    @Override
    public Iterator<JobOffer> getJobOffersByCompany(String companyId) throws NOJobOffersException {
        Iterator<JobOffer> jobOffer = this.companies.get(companyId).getJobOffers();
        if (!jobOffer.hasNext()) {
            throw new NOJobOffersException();
        }

        return jobOffer;
    }

    @Override
    public Iterator<JobOffer> getAllJobOffers() throws NOJobOffersException {
        if (this.jobOffers.isEmpty()){
            throw new NOJobOffersException();
        }

        return this.jobOffers.values();
    }

    @Override
    public Iterator<JobOffer> getJobOffersByWorker(String workerId) throws NOJobOffersException {
        Worker worker = getWorker(workerId);
        Iterator<JobOffer> it = worker.getJobOffers();

        if (!it.hasNext()) {
            throw new NOJobOffersException();
        }
        return it;
    }

    @Override
    public void addRating(String workerId, String jobOfferId, int value, String message) throws WorkerNotFoundException, JobOfferNotFoundException, WorkerNOEnrolledException {
        Worker worker = getWorker(workerId);
        if (worker == null) {
            throw new WorkerNotFoundException();
        }

        JobOffer jobOffer = getJobOffer(jobOfferId);
        if (jobOffer == null) {
            throw new JobOfferNotFoundException();
        }

        if (!worker.isInJobOffer(jobOffer)) {
            throw new WorkerNOEnrolledException();
        }

        jobOffer.addRating(value, message, worker);

        updateBestJobOffer(jobOffer);
    }

    private void updateBestJobOffer(JobOffer jobOffer) {
        bestJobOffer.delete(jobOffer);
        bestJobOffer.update(jobOffer);
    }

    @Override
    public Iterator<Rating> getRatingsByJobOffer(String jobOfferId) throws JobOfferNotFoundException, NORatingsException {
        JobOffer jobOffer = getJobOffer(jobOfferId);
        if (jobOffer == null) {
            throw new JobOfferNotFoundException();
        }

        Iterator<Rating> it = jobOffer.getRatings();
        if (!it.hasNext()) {
            throw new NORatingsException();
        }

        return it;
    }

    @Override
    public Worker getMostActiveWorker() throws NoWorkerException {
        if (this.mostActiveWorker == null) {
            throw new NoWorkerException();
        }
        return this.mostActiveWorker;
    }

    @Override
    public JobOffer getBestJobOffer() throws NOJobOffersException {
        if (bestJobOffer.isEmpty()) {
            throw new NOJobOffersException();
        }
        return bestJobOffer.elementAt(0);
    }

    @Override
    public Worker getWorker(String id) {
        return this.workers.get(new Worker(id));
    }

    @Override
    public Company getCompany(String id) {
        return this.companies.get(id);
    }

    @Override
    public JobOffer getJobOffer(String jobOfferId) {
        return this.jobOffers.get(new JobOffer(jobOfferId));
    }

    @Override
    public int numWorkers() {
        return this.workers.size();
    }

    @Override
    public int numCompanies() {
        return this.companies.size();
    }

    @Override
    public int numJobOffers() {
        return this.jobOffers.size();
    }

    @Override
    public int numPendingRequests() {
        return this.requests.size();
    }

    @Override
    public int numTotalRequests() {
        return this.totalRequests;
    }

    @Override
    public int numRejectedRequests() {
        return this.rejectedRequests;
    }

    @Override
    public void addRole(String roleId, String description) {
        Role role = getRole(roleId);

        if (role != null) {
            role.update(description);
        } else {
            this.roles.put(roleId, new Role(roleId, description));
        }
    }

    @Override
    public void addEmployee(String employeeId, String name, String surname, LocalDate localDate, String role) {
        final Employee employee = new Employee(employeeId, name, surname, localDate, role);
        final Employee foundEmployee = getEmployee(employeeId);

        if(foundEmployee != null ) {
            if (foundEmployee.isNewRole(role)) {
                this.roles.get(foundEmployee.getRole()).removeEmployee(employee.getEmployeeId());
            }
            foundEmployee.update(name, surname, localDate, role);
        } else {
            this.employees.put(employeeId, employee);
            this.employeesNetworK.newVertex(employee);
        }

        if (!this.roles.get(role).isEmployeeAlreadyInRole(employeeId)) {
            this.roles.get(role).addEmployee(employee);
        }
    }

    @Override
    public void addRoom(String roomId, String name, String description, RoomType roomtype) {
        Room room = getRoom(roomId);

        if (room != null) {
            room.update(name, description, roomtype);
        }
        this.rooms.put(roomId, new Room(roomId, name, description, roomtype));
        this.roomsWithoutEmployees.insertBeginning(room);
    }

    @Override
    public void assignEmployee(String employeeId, String roomId) throws EmployeeAlreadyAssignedException, EmployeeNotFoundException, RoomNotFoundException {
        Employee employee = getEmployee(employeeId);

        if (employee == null) {
            throw new EmployeeNotFoundException();
        }

        Room room = getRoom(roomId);

        if (room == null) {
            throw new RoomNotFoundException();
        }
        if (employee.isAssignedToRoom(room)) {
            throw new EmployeeAlreadyAssignedException();
        }

        room.addEmployee(employee);
        employee.addRoom(room);

        if (room.getAssignedEmployees().isEmpty()) {
            deleteRoomWithoutEmployeesFromList(roomId);
        }
    }

    private void deleteRoomWithoutEmployeesFromList(String roomId){
        LinkedList<Room> newList = new LinkedList<>();
        LinkedList<Room> aux = this.roomsWithoutEmployees;
        while (!aux.isEmpty()) {
            Room cur = aux.deleteFirst();
            if (cur != null && !cur.getRoomId().equals(roomId)) {
                newList.insertBeginning(cur);
            }
        }

        this.roomsWithoutEmployees = newList;
    }

    @Override
    public Iterator<Employee> getEmployeesByRoom(String roomId) throws RoomNotFoundException, NOEmployeeException {
        Room foundRoom = getRoom(roomId);

        if (foundRoom == null) {
            throw new RoomNotFoundException();
        }
        if (foundRoom.getAssignedEmployees().isEmpty()) {
            throw new NOEmployeeException();
        }

        return this.rooms.get(roomId).getAssignedEmployees().values();
    }

    @Override
    public Iterator<Employee> getEmployeesByRole(String roleId) throws NOEmployeeException {
        LinkedList<Employee> employees = getRole(roleId).getEmployees();

        if (employees.isEmpty()) {
            throw new NOEmployeeException();
        }

        return employees.values();
    }

    @Override
    public void addEquipment(String equipmentId, String name, String description) {
        Equipment foundEquipment = getEquipment(equipmentId);

        if (foundEquipment != null) {
            foundEquipment.update(name, description);
        } else {
            Equipment equipment = new Equipment(equipmentId, name, description);
            this.equipments.add(equipment);
        }
    }

    @Override
    public AssignEquipmentResponse assignEquipment(String equipmentId, String roomId) throws EquipmentNotFoundException, RoomNotFoundException, EquipmentAlreadyAssignedException {
        Equipment foundEquipment = getEquipment(equipmentId);
        Room foundRoom = getRoom(roomId);
        AssignEquipmentResponse response = AssignEquipmentResponse.ASSIGNED;

        if(foundEquipment == null) {
            throw new EquipmentNotFoundException();
        }
        if(foundRoom == null) {
            throw new RoomNotFoundException();
        }
        if (foundEquipment.isInRoom(roomId)) {
            throw new EquipmentAlreadyAssignedException();
        }

        if (foundEquipment.getRoom() != null && !foundEquipment.isInRoom(roomId)) {
            Room oldRoom = foundEquipment.getRoom();
            this.getRoom(oldRoom.getRoomId()).removeEquipment(foundEquipment.getEquipmentId());
            response = AssignEquipmentResponse.REASSIGNED;
        }

        foundRoom.addEquipment(foundEquipment);
        foundEquipment.setRoom(foundRoom);
        updateMostEquippedRooms(foundRoom);

        return response;
    }

    private void updateMostEquippedRooms(Room room) {
        mostEquippedRooms.delete(room);
        mostEquippedRooms.update(room);
    }

    @Override
    public Level getLevel(String workerId) throws WorkerNotFoundException {
        Worker foundWorker = getWorker(workerId);

        if (foundWorker == null) {
            throw new WorkerNotFoundException();
        }
        return foundWorker.getLevel();
    }

    @Override
    public Iterator<Enrollment> getWorkersByJobOffer(String jobOfferId) throws JobOfferNotFoundException, NoWorkerException {
        JobOffer jobOffer = getJobOffer(jobOfferId);

        if (jobOffer == null) {
            throw new JobOfferNotFoundException();
        }
        if (!jobOffer.enrollments().hasNext()) {
            throw new NoWorkerException();
        }

        return jobOffer.enrollments();
    }

    @Override
    public Iterator<Enrollment> getSubstitutesByJobOffer(String jobOfferId) throws JobOfferNotFoundException, NoWorkerException {
        JobOffer jobOffer = getJobOffer(jobOfferId);

        if (jobOffer == null) {
            throw new JobOfferNotFoundException();
        }
        if (!jobOffer.substitutes().hasNext()) {
            throw new NoWorkerException();
        }

        return jobOffer.substitutes();
    }

    @Override
    public Iterator<Room> getRoomsWithoutEmployees() throws NoRoomsException {
        if (this.roomsWithoutEmployees.isEmpty() || this.rooms.isEmpty()) {
            throw new NoRoomsException();
        }

        Iterator<Room> rooms = this.rooms.values();
        LinkedList<Room> emptyRooms = new LinkedList<>();

        while (rooms.hasNext()) {
            Room cur = rooms.next();
            if (cur.getAssignedEmployees().isEmpty()) {
                emptyRooms.insertBeginning(cur);
            }
        }

        return emptyRooms.values();
    }

    @Override
    public Iterator<Room> best5EquippedRooms() throws NoRoomsException {
        if (mostEquippedRooms.isEmpty()) {
            throw new NoRoomsException();
        }
        return this.mostEquippedRooms.values();
    }

    @Override
    public void addFollower(String followerId, String followedId) throws FollowerNotFound, FollowedException {
        Employee follower = getEmployee(followedId);
        Vertex<Employee> followerVertex = this.employeesNetworK.getVertex(follower);
        if (followerVertex == null) {
            throw new FollowerNotFound();
        }

        Employee followed = getEmployee(followedId);
        Vertex<Employee> followedVertex = this.employeesNetworK.getVertex(followed);
        if (followedVertex == null) {
            throw new FollowedException();
        }

        Edge<String, Employee> edgeFollower = this.employeesNetworK.newEdge(followerVertex, followedVertex);
        edgeFollower.setLabel("follower");
        Edge<String, Employee> edgeFollowed = this.employeesNetworK.newEdge(followedVertex, followerVertex);
        edgeFollowed.setLabel("followed");
    }

    @Override
    public Iterator<Employee> getFollowers(String followedId) throws EmployeeNotFoundException, NoFollowersException, FollowerNotFound {
        Employee followed = getEmployee(followedId);

        if (followed == null) {
            throw new EmployeeNotFoundException();
        }

        if (!this.employeesNetworK.edges().hasNext()) {
            throw new NoFollowersException();
        }

        DirectedVertexImpl<Employee, String> employeeVertex = (DirectedVertexImpl<Employee, String>) this.employeesNetworK.getVertex(followed);
        Iterator<Edge<String, Employee>> it = employeeVertex.edges();

        LinkedList<Employee> employeeLinkedList = new LinkedList<>();

        while (it.hasNext()) {
            Employee cur = ((DirectedEdge<String, Employee>) it.next()).getVertexDst().getValue();
            //employeeLinkedList.add(cur);
            employeeLinkedList.insertBeginning(cur);
        }
        
        return employeeLinkedList.values();
    }

    @Override
    public Iterator<Employee> getFollowings(String followerId) throws EmployeeNotFoundException, NoFollowedException {
        return null;
    }

    @Override
    public Iterator<Employee> recommendations(String followerId) throws EmployeeNotFoundException, NoFollowedException {
        return null;
    }

    @Override
    public Iterator<Employee> getUnfollowedColleagues(String employeeId) throws EmployeeNotFoundException, NOEmployeeException {
        return null;
    }

    @Override
    public int numRoles() {
        return this.roles.size();
    }

    @Override
    public int numEmployees() {
        return this.employees.size();
    }

    @Override
    public int numEmployeesByRole(String roleId) {
        return this.roles.get(roleId).getEmployees().size();
    }

    @Override
    public int numRooms() {
        return rooms.size();
    }

    @Override
    public int numEquipments() {
        return this.equipments.size();
    }

    @Override
    public int numEquipmentsByRoom(String roomId) {
        return getRoom(roomId).getEquipments().size();
    }

    @Override
    public int numFollowers(String employeeId) {
        int size = 0;
        try {
            Iterator<Employee> it = getFollowers(employeeId);
            while (it.hasNext()) {
                it.next();
                size++;
            }
        } catch (DSException e) {
            throw new RuntimeException(e);
        }
        return size;
    }

    private int countEdges(Iterator<Vertex<Employee>> vertexIterator) {
        int size = 0;
        while (vertexIterator.hasNext()) {
            size++;
            vertexIterator.next();
        }
        return size;
    }

    @Override
    public int numFollowings(String employeeId) {
        return 0;
    }

    @Override
    public int numRoomsByEmployee(String employee) {
        return getEmployee(employee).getRooms().size();
    }

    @Override
    public Room whereIs(String equipmentId) {
        return getEquipment(equipmentId).getRoom();
    }

    @Override
    public Role getRole(String role) {
        return this.roles.get(role);
    }

    @Override
    public Employee getEmployee(String employeeId) {
        return this.employees.get(employeeId);
    }

    @Override
    public Room getRoom(String roomId) {
        return this.rooms.get(roomId);
    }

    @Override
    public Equipment getEquipment(String equipmentId) {
        return this.equipments.get(new Equipment(equipmentId));
    }
}
