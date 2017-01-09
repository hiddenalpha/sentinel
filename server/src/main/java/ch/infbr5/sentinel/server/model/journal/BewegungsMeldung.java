package ch.infbr5.sentinel.server.model.journal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import ch.infbr5.sentinel.server.model.OperatorAktion;
import ch.infbr5.sentinel.server.model.Person;

@Entity
@NamedQueries({
   @NamedQuery(name = "findBewegungsMeldung", query = "SELECT r FROM BewegungsMeldung r WHERE r.id = :id"),
   @NamedQuery(name = "findBewegungsMeldungen", query = "SELECT r FROM BewegungsMeldung r ORDER BY r.millis DESC"),
   @NamedQuery(name = "findBewegungsMeldungenSeit", query = "SELECT r FROM BewegungsMeldung r WHERE r.millis > :timeInMillis order by r.millis desc"), })
public class BewegungsMeldung extends JournalEintrag {

   private OperatorAktion operatorAktion;

   @ManyToOne
   private Person person;

   public OperatorAktion getOperatorAktion() {
      return operatorAktion;
   }

   public void setOperatorAktion(final OperatorAktion operatorAktion) {
      this.operatorAktion = operatorAktion;
   }

   public Person getPerson() {
      return person;
   }

   public void setPerson(final Person person) {
      this.person = person;
   }

}
