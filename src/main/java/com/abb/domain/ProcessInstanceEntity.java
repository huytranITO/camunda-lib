package com.abb.domain;

import com.abb.enums.ProcessInstanceType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "process_instance")
@Getter
@Setter
@ToString
@With
@AllArgsConstructor
@NoArgsConstructor
public class ProcessInstanceEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @Basic
  @Column(name = "process_instance_type")
  @Enumerated(EnumType.STRING)
  private ProcessInstanceType processInstanceType;

  @Basic
  @Column(name = "process_instance_id")
  private String processInstanceId;

  @Basic
  @Column(name = "process_definition_id")
  private String processDefinitionId;

  @Basic
  @Column(name = "process_business_key")
  private String processBusinessKey;

  @Basic
  @Column(name = "process_key")
  private String processKey;

  @Basic
  @Column(name = "workflow_version")
  private Integer workflowVersion;

  @Basic
  @Column(name = "sub_process_instance_id")
  private String subProcessInstanceId;

  @Basic
  @Column(name = "sub_process_definition_id")
  private String subProcessDefinitionId;

  @Basic
  @Column(name = "sub_process_key")
  private String subProcessKey;

  @Basic
  @Column(name = "sub_workflow_version")
  private Integer subWorkflowVersion;

  @Basic
  @Column(name = "task_id")
  private String taskId;

  @Basic
  @Column(name = "task_definition_key")
  private String taskDefinitionKey;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

}
