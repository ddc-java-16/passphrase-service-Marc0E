package edu.cnm.deepdive.passphrase.service;

import edu.cnm.deepdive.passphrase.configuration.FileStorageConfiguration;
import edu.cnm.deepdive.passphrase.model.dao.AttachmentRepository;
import edu.cnm.deepdive.passphrase.model.dao.PassphraseRepository;
import edu.cnm.deepdive.passphrase.model.entity.Attachment;
import edu.cnm.deepdive.passphrase.model.entity.User;
import edu.cnm.deepdive.passphrase.service.StorageService.StorageException;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentService implements AbstractAttachmentService {

  private final AttachmentRepository attachmentRepository;
  private final PassphraseRepository passphraseRepository;
  private final StorageService service;
  private final FileStorageConfiguration configuration;

  @Autowired
  public AttachmentService(AttachmentRepository attachmentRepository, StorageService service,
      PassphraseRepository passphraseRepository, FileStorageConfiguration configuration) {
    this.attachmentRepository = attachmentRepository;
    this.service = service;
    this.passphraseRepository = passphraseRepository;
    this.configuration = configuration;
  }

  @NonNull
  @Override
  public Iterable<Attachment> readAll(@NonNull User user, @NonNull UUID passphraseKey) {
    return passphraseRepository
        .findByUserAndKey(user, passphraseKey)
        .map(passphrase -> passphrase.getAttachments())
        .orElseThrow();
  }

  @NonNull
  @Override
  public Attachment read(@NonNull User user, @NonNull UUID passphraseKey,
      @NonNull UUID attachmentKey) {
    return passphraseRepository
        .findByUserAndKey(user, passphraseKey)
        .flatMap(
            passphrase -> attachmentRepository.findByPassphraseAndKey(passphrase, attachmentKey))
        .orElseThrow();
  }

  @NonNull
  @Override
  public Resource readContent(@NonNull User user, @NonNull UUID passphraseKey,
      @NonNull UUID attachmentKey)
      throws StorageException {
    Attachment attachment = read(user, passphraseKey, attachmentKey);
    return service.retrieve(attachment.getStorageKey());
  }

  @NonNull
  @Override
  public Attachment store(@NonNull User user, @NonNull UUID passphraseKey,
      @NonNull MultipartFile file) throws StorageException {
    return passphraseRepository.findByUserAndKey(user, passphraseKey)
        .map(passphrase -> {
            String orignalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();
            String storageKey = service.store(file);
            long size = file.getSize();
            Attachment attachment = new Attachment();
            attachment.setFilename((orignalFilename != null) ? orignalFilename : configuration.getFilename().getUnknown());
            //noinspection DataFlowIssue
            attachment.setContentType(contentType);
            attachment.setSize(size);
            attachment.setPassphrase(passphrase);
            attachment.setStorageKey(storageKey);
            return attachmentRepository.save(attachment);
        })
        .orElseThrow();

  }

  @Override
  public void delete(@NonNull User user, @NonNull UUID passphraseKey, @NonNull UUID attachmentKey) {
    attachmentRepository.delete(
        passphraseRepository
        .findByUserAndKey(user, passphraseKey)
            .flatMap(passphrase -> attachmentRepository.findByPassphraseAndKey(passphrase, attachmentKey))
        .orElseThrow()
        );
  }
}
