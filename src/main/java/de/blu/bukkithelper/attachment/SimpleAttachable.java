package de.blu.bukkithelper.attachment;

import java.util.HashMap;
import java.util.Map;

public interface SimpleAttachable extends Attachable {

  Map<Class<? extends Attachment>, Attachment> attachments = new HashMap<>();

  @Override
  default void attach(Attachment attachment) {
    this.attachments.put(attachment.getClass(), attachment);
  }

  @Override
  default <T extends Attachment> T getAttachment(Class<? extends T> type) {
    return (T) this.attachments.getOrDefault(type, null);
  }
}
