package de.blu.bukkithelper.attachment;

public interface Attachable {

  /**
   * Attach the given Attachment to access it later
   *
   * @param attachment the addon attachment for this instance
   */
  void attach(Attachment attachment);

  /**
   * Get the Attachment by class
   *
   * @param type the class of the Attachment
   * @param <T> the type of the Attachment
   * @return the Attachment if found or null
   */
  <T extends Attachment> T getAttachment(Class<? extends T> type);
}
