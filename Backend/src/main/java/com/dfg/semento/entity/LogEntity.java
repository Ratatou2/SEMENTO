package com.dfg.semento.entity;

import com.dfg.semento.enums.Mode;
import com.dfg.semento.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class LogEntity {
    @Id
    @Column(name="doc_id")
    public String docId;

    @Column(name="oht_id")
    public Long ohtId;

    @Column(name="mode")
    public Mode mode;

    @Column(name="status")
    public Status status;

    @Column(name="current_node")
    public String currentNode;

    @Column(name="next_node")
    public String nextNode;

    @Column(name="target_node")
    public String targetNode;

    @Column(name="carrier")
    public Boolean carrier;

    @Column(name="error")
    public int error;

    @Column(name="oht_connect")
    public boolean ohtConnect;

    @Column(name="curr_node_offset")
    public double currNodeOffset;

    @Column(name="speed")
    public double speed;

    @Column(name="is_fail")
    public boolean isFail;

    @Column(name="curr_time")
    public LocalDateTime currTime;

    @Column(name="start_time")
    public LocalDateTime startTime;

    @Column(name="point_x")
    public double pointX;

    @Column(name="point_y")
    public double pointY;

    @Column(name="path")
    public String path;

}
