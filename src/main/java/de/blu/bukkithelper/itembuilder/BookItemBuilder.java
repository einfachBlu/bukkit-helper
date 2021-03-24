package de.blu.bukkithelper.itembuilder;

import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;

public final class BookItemBuilder extends ItemBuilder {

  public BookItemBuilder() {
    super();
    this.setType(Material.WRITTEN_BOOK);
  }

  public BookItemBuilder setWriteable() {
    return this.setWriteable(true);
  }

  public BookItemBuilder setWriteable(boolean value) {
    BookMeta bookMeta = (BookMeta) this.getItemMeta();
    if (value) {
      this.setType(Material.BOOK_AND_QUILL);
    } else {
      this.setType(Material.WRITTEN_BOOK);
    }
    this.setItemMeta(bookMeta);

    return this;
  }

  public BookItemBuilder setTitle(String title) {
    BookMeta bookMeta = (BookMeta) this.getItemMeta();
    bookMeta.setTitle(title);

    return this;
  }

  public BookItemBuilder setAuthor(String author) {
    BookMeta bookMeta = (BookMeta) this.getItemMeta();
    bookMeta.setAuthor(author);

    return this;
  }

  public BookItemBuilder addPage(String... data) {
    BookMeta bookMeta = (BookMeta) this.getItemMeta();
    bookMeta.addPage(data);

    return this;
  }

  public BookItemBuilder setPage(int i, String data) {
    BookMeta bookMeta = (BookMeta) this.getItemMeta();

    if (bookMeta.getPageCount() <= i) {
      for (int i2 = 0; i2 < i; i2++) {
        if (bookMeta.getPageCount() <= i2) {
          this.addPage(" ");
        }
      }
    }

    bookMeta.setPage(i, data);
    return this;
  }

  public BookItemBuilder setPages(String... data) {
    BookMeta bookMeta = (BookMeta) this.getItemMeta();
    bookMeta.setPages(data);

    return this;
  }
}
