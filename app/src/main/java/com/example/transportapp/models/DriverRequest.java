package com.example.transportapp.models;

import java.io.Serializable;
import java.util.Date;

public class DriverRequest implements Serializable {

    String id;
    String driverId;
    String status;
    String managerName;
    String observations;
    Date created_at;
    Date update_at;

    public DriverRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.id = managerName;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt() {this.created_at = new Date();}

    public Date getUpdatedAt() {return update_at;}

    public void setUpdateAt() {
        this.update_at = new Date();
    }
}
