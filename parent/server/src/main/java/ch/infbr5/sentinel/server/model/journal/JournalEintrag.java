package ch.infbr5.sentinel.server.model.journal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import ch.infbr5.sentinel.server.model.Checkpoint;

@Entity
@NamedQueries({ @NamedQuery(name = "findJournalEintragById", query = "SELECT r FROM JournalEintrag r WHERE r.id = :id"), })
public class JournalEintrag {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   private long millis;

   @ManyToOne
   private Checkpoint checkpoint;

   public long getId() {
      return id;
   }

   public void setId(final long id) {
      this.id = id;
   }

   public long getMillis() {
      return millis;
   }

   public void setMillis(final long millis) {
      this.millis = millis;
   }

   public Checkpoint getCheckpoint() {
      return checkpoint;
   }

   public void setCheckpoint(final Checkpoint checkpoint) {
      this.checkpoint = checkpoint;
   }

}
