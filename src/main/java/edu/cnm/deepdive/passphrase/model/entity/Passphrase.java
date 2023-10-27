package edu.cnm.deepdive.passphrase.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
public class Passphrase {

  @Id
  private long id;

  private UUID key;

  @CreationTimestamp
  private Instant created;

  @UpdateTimestamp
  private Instant modified;

  private String name;

  @Transient
  private transient int length;

  public long getId() {
    return id;
  }

  public UUID getKey() {
    return key;
  }

  public Instant getCreated() {
    return created;
  }

  public Instant getModified() {
    return modified;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }
}
