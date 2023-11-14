package edu.cnm.deepdive.passphrase.model.dao;

import edu.cnm.deepdive.passphrase.model.entity.Attachment;
import edu.cnm.deepdive.passphrase.model.entity.Passphrase;
import edu.cnm.deepdive.passphrase.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

  //@Query("SELECT ALL FROM Attachment AS a JOIN Passphrase AS p ON a.passphrase ON p.user WHERE a.key = :key AND a.passphrase = :passphrase and p.user = :user")
  Optional<Attachment>findByPassphraseAndKey(Passphrase passphrase, UUID key);

  Iterable<Attachment> findAllByPassphraseOrderByCreatedDesc(Passphrase passphrase);



}
