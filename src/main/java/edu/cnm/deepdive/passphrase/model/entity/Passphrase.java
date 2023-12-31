package edu.cnm.deepdive.passphrase.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.cnm.deepdive.passphrase.validation.ValidPassphraseLength;
import edu.cnm.deepdive.passphrase.view.UUIDSerializer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.print.attribute.standard.MediaSize.NA;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.NonNull;

@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "name"})
    }
)
@ValidPassphraseLength
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({"ide","created","modified","name"})
public class Passphrase {

  @Id
  @NonNull
  @Column(name = "passphrase_id", updatable = false)
  @GeneratedValue
  @JsonIgnore
  private Long id;

  @NonNull
  @Column(name = "external_key",updatable = false, nullable = false, unique = true, columnDefinition = "UUID")
  @JsonProperty(value = "id",access = Access.READ_ONLY)
  @JsonSerialize(converter = UUIDSerializer.class)
  private UUID key;

  @CreationTimestamp
  @NonNull
  @Temporal(TemporalType.TIMESTAMP)
  @Column(updatable = false, nullable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Instant created;

  @NonNull
  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Instant modified;

  @NonNull
  @Column(nullable = false)
  @NotBlank
  @NotNull
  private String name;

  @NonNull
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, updatable = false)
  @JsonIgnore
  private User user;

  @NonNull
  @OneToMany(mappedBy = "passphrase", fetch = FetchType.EAGER,
      cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("order ASC")
  private List<Word> words = new LinkedList<>();

  @NonNull
  @OneToMany(mappedBy = "passphrase", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("created DESC")
  private final List<Attachment> attachments = new LinkedList<>();

  @Transient
  @JsonProperty(access = Access.READ_WRITE)
  private int length;

  @NonNull
  public Long getId() {
    return id;
  }

  @NonNull
  public UUID getKey() {
    return key;
  }

  @NonNull
  public Instant getCreated() {
    return created;
  }

  @NonNull
  public Instant getModified() {
    return modified;
  }

  @NonNull
  public String getName() {
    return name;
  }

  public void setName(@NonNull String name) {
    this.name = name;
  }

  @NonNull
  public User getUser() {
    return user;
  }

  public void setUser(@NonNull User user) {
    this.user = user;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  @NonNull
  public List<Word> getWords() {
    return words;
  }



  public List<Attachment> getAttachments() {
    return attachments;
  }

  @PrePersist
  private void generateKey(){
    key = UUID.randomUUID();
  }
}
