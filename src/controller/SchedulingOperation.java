package controller;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Abisheik on 7/15/2016.
 */
public class SchedulingOperation {

    private Integer operationId;

    private String operationName;

    private boolean hasPredecessor;

    private boolean hasSuccessor;

    private Integer startTime;

    private boolean scheduled;

    private Integer delayTime;

    private String[] predecessorList ;

    private String[] successorList ;

    private Integer scheduledBy;

    // Getters and setters

    public Integer getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Integer delayTime) {
        this.delayTime = delayTime;
    }

    public boolean isHasSuccessor() {
        return hasSuccessor;
    }

    public void setHasSuccessor(boolean hasSuccessor) {
        this.hasSuccessor = hasSuccessor;
    }

    public String[] getSuccessorList() {
        return successorList;
    }

    public void setSuccessorList(String[] successorList) {
        this.successorList = successorList;
    }

    public String[] getPredecessorList() {
        return predecessorList;
    }

    public void setPredecessorList(String[] predecessorList) {
        this.predecessorList = predecessorList;
    }

    public Integer getOperationId() {
        return operationId;
    }

    public void setOperationId(Integer operationId) {
        this.operationId = operationId;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public boolean isHasPredecessor() {
        return hasPredecessor;
    }

    public void setHasPredecessor(boolean hasPredecessor) {
        this.hasPredecessor = hasPredecessor;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public Integer getScheduledBy() {
        return scheduledBy;
    }

    public void setScheduledBy(Integer scheduledBy) {
        this.scheduledBy = scheduledBy;
    }

    @Override
    public String toString() {
        return "SchedulingOperation{" +
                "operationId=" + operationId +
                ", operationName='" + operationName + '\'' +
                ", hasPredecessor=" + hasPredecessor +
                ", startTime=" + startTime +
                ", delayTime=" + delayTime +
                ", scheduled=" + scheduled +
                ", predecessorList=" + Arrays.toString(predecessorList) +
                '}';
    }
}
