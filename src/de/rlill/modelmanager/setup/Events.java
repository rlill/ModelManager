package de.rlill.modelmanager.setup;

import java.util.ArrayList;
import java.util.List;

import de.rlill.modelmanager.model.Event;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.EventIcon;

public class Events {

	public final static int SYSTEM_ID_CAR_ACCIDENT_COST = 1069;

	public static List<Event> getEvents() {
		List<Event> result = new ArrayList<Event>();

		Event event = new Event(1000, 1);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.NEWDAY);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Tagesabschluß.");
		result.add(event);

		event = new Event(1001, 2);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.PAYCHECK);
		event.setIcon(EventIcon.MONEY);
		event.setDescription("%N hat heute ihr Gehalt in Höhe von %A bekommen.");
		event.setNoteAcct("Gehalt");
		result.add(event);

		event = new Event(1002, 1);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.QUIT);
		event.setIcon(EventIcon.DOOR);
		event.setDescription("%N hat gekündigt.");
		result.add(event);

		event = new Event(1003, 1);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.SICK);
		event.setIcon(EventIcon.SICKNESS);
		event.setDescription("%N hat sich heute krank gemeldet.");
		result.add(event);

		event = new Event(1004, 3);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.CAR_BROKEN);
		event.setIcon(EventIcon.CAR);
		event.setDescription("%N hat einen Blechschaden mit ihrem Auto (%C) verursacht. Es muß instandgesetzt werden.");
		result.add(event);

		event = new Event(1005, 3);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.CAR_STOLEN);
		event.setIcon(EventIcon.CAR);
		event.setDescription("%N wurde ihr Auto (%C) gestohlen. Um wieder für Fototermine gebucht werden zu können, braucht sie ein neues Auto.");
		result.add(event);

		event = new Event(1006, 2);
		event.setEclass(EventClass.BOOKING);
		event.setFlag(EventFlag.PHOTO);
		event.setIcon(EventIcon.CAMERA);
		event.setDescription("%N soll heute für %A für ein Fotoshooting gebucht werden.");
		event.setNoteFile("Gebucht für Fotoshooting, Gage %A");
		event.setNoteAcct("Gage für Fotoshooting %N");
		event.setAmountMin(1000);
		event.setAmountMax(2000);
		event.setChance(100);
		result.add(event);

		event = new Event(1007, 5);
		event.setEclass(EventClass.BOOKING);
		event.setFlag(EventFlag.MOVIE);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("%N soll heute für %A für den Dreh eines Films gebucht werden.");
		event.setNoteFile("Gebucht für Dreharbeiten, Gage %A");
		event.setNoteAcct("Gage für Dreharbeiten %N");
		event.setAmountMin(1500);
		event.setAmountMax(3000);
		result.add(event);

		event = new Event(1008, 2);
		event.setEclass(EventClass.REQUEST);
		event.setFlag(EventFlag.RAISE);
		event.setIcon(EventIcon.MONEY);
		event.setDescription("%N möchte eine Gehaltserhöhung von %S auf %A pro Woche.");
		event.setNoteFile("Gehaltserhöhung von %S auf %A");
		result.add(event);

		event = new Event(1009, 2);
		event.setEclass(EventClass.REQUEST);
		event.setFlag(EventFlag.BONUS);
		event.setIcon(EventIcon.MONEY);
		event.setDescription("%N fordert eine Prämie in Höhe von %A.");
		event.setNoteFile("Prämie %A");
		event.setNoteAcct("Prämie %N");
		result.add(event);

		event = new Event(1010, 1);
		event.setEclass(EventClass.REQUEST);
		event.setFlag(EventFlag.QUIT);
		event.setIcon(EventIcon.DOOR);
		event.setDescription("%N will kündigen.");
		event.setNoteFile("Kündigung");
		result.add(event);

		event = new Event(1011, 2);
		event.setEclass(EventClass.REQUEST);
		event.setFlag(EventFlag.VACATION);
		event.setIcon(EventIcon.PALMTREE);
		event.setDescription("%N möchte Urlaub nehmen. Sie hat %V Tage Resturlaub.");
		event.setNoteFile("Urlaub");
		result.add(event);

		event = new Event(1012, 1);
		event.setEclass(EventClass.REQUEST);
		event.setFlag(EventFlag.TRAINING);
		event.setIcon(EventIcon.EDUCATION);
		event.setDescription("%N möchte eine Fortbildung besuchen.");
		event.setNoteFile("Fortbildung");
		result.add(event);

		event = new Event(1013, 3);
		event.setEclass(EventClass.REQUEST);
		event.setFlag(EventFlag.CAR_UPDATE);
		event.setIcon(EventIcon.CAR);
		event.setDescription("%N möchte ein neues Auto. Derzeitiges Fahrzeug: %C");
		event.setNoteFile("Neues Auto");
		result.add(event);

		event = new Event(1014, 3);
		event.setEclass(EventClass.APPLICATION);
		event.setFlag(EventFlag.HIRE);
		event.setIcon(EventIcon.CONTRACT);
		event.setDescription("%N bewirbt sich um eine Anstellung. Sie möchte %A Gehalt pro Woche.");
		event.setNoteFile("Bewerbung");
		event.setAmountMin(1000);
		result.add(event);

		event = new Event(1015, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Bei der Beseitigung der Leiche eines Drogendealers fallen Ihnen %A in die Hände.");
		event.setNoteAcct("Bareinzahlung");
		event.setAmountMin(50000);
		event.setAmountMax(250000);
		event.setChance(40);
		result.add(event);

		event = new Event(1016, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Nach einer Party in Ihrem Haus bleibt ein anonymes Geldbündel von %A liegen.");
		event.setNoteAcct("Bareinzahlung");
		event.setAmountMin(50000);
		event.setAmountMax(250000);
		event.setChance(30);
		result.add(event);

		event = new Event(1017, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Für einen guten Bekannten gibt %N eine Sondervorstellung. Dieser bezahlt dafür %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage für Sondervorstellung mit %N");
		event.setAmountMin(10000);
		event.setAmountMax(40000);
		event.setChance(60);
		result.add(event);

		event = new Event(1018, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Für einen wichtigen Politiker gibt %N eine Sondervorstellung. Dieser bezahlt dafür %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage für Sondervorstellung mit %N");
		event.setAmountMin(20000);
		event.setAmountMax(60000);
		event.setChance(50);
		result.add(event);

		event = new Event(1019, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N gibt für einen Wirtschaftsboss eine Sondervorstellung. Dieser bezahlt dafür %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage für Sondervorstellung mit %N");
		event.setAmountMin(10000);
		event.setAmountMax(40000);
		event.setChance(50);
		result.add(event);

		event = new Event(1020, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N gibt für einen Börsenmakler eine Sondervorstellung. Dieser bezahlt dafür %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage für Sondervorstellung mit %N");
		event.setAmountMin(20000);
		event.setAmountMax(60000);
		event.setChance(60);
		result.add(event);

		event = new Event(1021, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N wird Model des Jahres in einem Männermagazin. Als Manager erhalten Sie einen Anteil des Preises in Höhe von %A");
		event.setNoteFile("Preiskrönung Model des Jahres");
		event.setNoteAcct("Preisgeldanteil %N (Model des Jahres)");
		event.setAmountMin(2000);
		event.setAmountMax(10000);
		event.setChance(50);
		result.add(event);

		event = new Event(1022, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N begleitet einen reichen Kunden auf einen Ball. Dieser bezahlt dafür %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage für Begleitservice durch %N");
		event.setAmountMin(5000);
		event.setAmountMax(15000);
		event.setChance(80);
		result.add(event);

		event = new Event(1023, 3);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N begleitet einen Scheich zum Golfen. Dieser bezahlt dafür %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage für Begleitservice durch %N");
		event.setAmountMin(25000);
		event.setAmountMax(80000);
		event.setChance(60);
		result.add(event);

		event = new Event(1024, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N begleitet einen Millionär ins Theater. Dieser bezahlt dafür %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage für Begleitservice durch %N");
		event.setAmountMin(10000);
		event.setAmountMax(25000);
		event.setChance(80);
		result.add(event);

		event = new Event(1025, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N begleitet einen Börsenmakler auf einen Ball. Dieser bezahlt dafür %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage für Begleitservice durch %N");
		event.setAmountMin(5000);
		event.setAmountMax(15000);
		event.setChance(70);
		result.add(event);

		event = new Event(1026, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N begleitet einen Wirtschaftsboss zum Golfen. Dieser bezahlt dafür %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage für Begleitservice durch %N");
		event.setAmountMin(4000);
		event.setAmountMax(8000);
		event.setChance(70);
		result.add(event);

		event = new Event(1027, 3);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N begleitet einen Scheich ins Theater. Dieser bezahlt dafür %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage für Begleitservice durch %N");
		event.setAmountMin(25000);
		event.setAmountMax(80000);
		event.setChance(50);
		result.add(event);

		event = new Event(1028, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Dafür, daß Sie mit einer Gruppe Models auf seiner Party für Stimmung gesorgt haben, gibt Ihnen ein Bekannter %A");
		event.setNoteAcct("Partyservice");
		event.setAmountMin(5000);
		event.setAmountMax(25000);
		event.setChance(60);
		result.add(event);

		event = new Event(1029, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Sie lassen einen Kumpel eine Party in Ihrem Haus schmeißen. Er gibt ihnen dafür %A");
		event.setNoteAcct("Partyservice");
		event.setAmountMin(5000);
		event.setAmountMax(25000);
		event.setChance(50);
		result.add(event);

		event = new Event(1030, 2);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Eine neugierige Amtsperson stellt zu viele Fragen. Für einen Obulus von %A gibt sie sich zufrieden.");
		event.setNoteAcct("Barauszahlung");
		event.setAmountMin(10000);
		event.setAmountMax(30000);
		event.setChance(20);
		event.setMaxpercent(20);
		result.add(event);

		event = new Event(1031, 3);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Sie werden wegen Steuerhinterziehung angeklagt. Ihr Anwalt hilft Ihnen, Ihre Unschuld zu beweisen und will %A dafür.");
		event.setNoteAcct("Anwaltskosten Steuerhinterziehungsklage");
		event.setAmountMin(5000);
		event.setAmountMax(10000);
		event.setChance(15);
		result.add(event);

		event = new Event(1032, 2);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Sie werden wegen Urheberrechtsverletzung angeklagt. Ihr Anwalt hilft Ihnen, Ihre Unschuld zu beweisen und will %A dafür.");
		event.setNoteAcct("Anwaltskosten Urheberrechtsklage");
		event.setAmountMin(4000);
		event.setAmountMax(8000);
		event.setChance(15);
		result.add(event);

		event = new Event(1033, 2);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Wegen eines Verstoßes gegen die Wettbewerbsregeln müssen Sie einem Konkurrenten %A Schadensersatz leisten.");
		event.setNoteAcct("Schadensersatz Konkurrenz");
		event.setAmountMin(4000);
		event.setAmountMax(25000);
		event.setMaxpercent(5);
		event.setChance(20);
		result.add(event);

		event = new Event(1034, 3);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat bei einem Fotoshooting mutwillig eine Kamera beschädigt. Die Reparatur kostet %A.");
		event.setNoteFile("Mutwillig Kamera beschädigt");
		event.setNoteAcct("Schadensersatz Kamera beschädigt durch %N");
		event.setAmountMin(12000);
		event.setAmountMax(33000);
		event.setChance(25);
		result.add(event);

		event = new Event(1035, 3);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Bei einem Fotoshooting hat %N mutwillig mehrere Scheinwerfer beschädigt. Der Preis für den Ersatz beläuft sich auf %A.");
		event.setNoteFile("Mutwillig Scheinwerfer beschädigt");
		event.setNoteAcct("Schadensersatz Scheinwerfer beschädigt durch %N");
		event.setAmountMin(10000);
		event.setAmountMax(40000);
		event.setChance(25);
		result.add(event);

		event = new Event(1036, 4);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat bei einem Fotoshooting den Fotografen geschlagen. Dieser klagt auf %A Schmerzensgeld.");
		event.setNoteFile("Fotografen geschlagen");
		event.setNoteAcct("Schmerzensgeld Fotograf geschlagen von %N");
		event.setAmountMin(10000);
		event.setAmountMax(20000);
		event.setChance(25);
		result.add(event);

		event = new Event(1037, 4);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("bei einem Fotoshooting hat %N einen Statisten gebissen. Dieser klagt auf %A Schmerzensgeld.");
		event.setNoteFile("Fotografen gebissen");
		event.setNoteAcct("Schmerzensgeld Statist gebissen von %N");
		event.setAmountMin(10000);
		event.setAmountMax(40000);
		event.setChance(25);
		result.add(event);

		event = new Event(1038, 3);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat den Studioboden mit ihren Schuhen ruiniert. Es muß für %A neuer Parkett verlegt werden.");
		event.setNoteFile("Studioeinrichtung beschädigt");
		event.setNoteAcct("Schadensersatz Studioboden beschädigt durch %N");
		event.setAmountMin(12000);
		event.setAmountMax(60000);
		event.setChance(50);
		result.add(event);

		event = new Event(1039, 3);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Bei einem Fotoshooting hat %N ihre Designerschuhe ruiniert. Sie müssen sie für %A ersetzen.");
		event.setNoteFile("Schuhe beschädigt");
		event.setNoteAcct("Schadensersatz Schuhe %N");
		event.setAmountMin(10000);
		event.setAmountMax(20000);
		event.setChance(50);
		result.add(event);

		event = new Event(1040, 3);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Bei einem Fotoshooting hat %N ihr Kleid ruiniert. Sie müssen es für %A ersetzen.");
		event.setNoteFile("Kleid beschädigt");
		event.setNoteAcct("Schadensersatz Kleid %N");
		event.setAmountMin(5000);
		event.setAmountMax(15000);
		event.setChance(25);
		result.add(event);

		event = new Event(1041, 3);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Bei einem Fotoshooting hat %N ihren Badeanzug ruiniert. Sie müssen ihn für %A ersetzen.");
		event.setNoteFile("Badeanzug beschädigt");
		event.setNoteAcct("Schadensersatz Badenazug %N");
		event.setAmountMin(500);
		event.setAmountMax(2000);
		event.setChance(25);
		result.add(event);

		event = new Event(1042, 3);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Bei einem Fotoshooting hat %N ihre Handtasche ruiniert. Sie müssen sie für %A ersetzen.");
		event.setNoteFile("Handtasche beschädigt");
		event.setNoteAcct("Schadensersatz Handtasche %N");
		event.setAmountMin(2000);
		event.setAmountMax(8000);
		event.setChance(25);
		result.add(event);

		event = new Event(1043, 4);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat bei einem Fotoshooting den Fotografen schwer beleidigt. Dieser klagt auf %A Schmerzensgeld.");
		event.setNoteFile("Fotografen beleidigt");
		event.setNoteAcct("Schmerzensgeld Fotograf beleidigt von %N");
		event.setAmountMin(4000);
		event.setAmountMax(12000);
		event.setChance(25);
		result.add(event);

		event = new Event(1044, 4);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Bei einem Fotoshooting hat %N einen Statisten geschlagen. Dieser klagt auf %A Schmerzensgeld.");
		event.setNoteFile("Statisten geschlagen");
		event.setNoteAcct("Schmerzensgeld Statist gschlagen von %N");
		event.setAmountMin(6000);
		event.setAmountMax(12000);
		event.setChance(25);
		result.add(event);

		event = new Event(1045, 4);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N wird wegen Verstoßes gegen das Betäubungsmittelgesetz angeklagt. Ihr Anwalt holt sie für %A da raus.");
		event.setNoteFile("Drogenmissbrauch");
		event.setNoteAcct("Anwaltskosten Anklage gegen %N");
		event.setAmountMin(20000);
		event.setAmountMax(40000);
		event.setChance(40);
		result.add(event);

		event = new Event(1046, 6);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.PAYVAR_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N will peinliche Fotos von Ihnen veröffentlichen, wenn Sie ihr nicht %A geben.");
		event.setNoteFile("Erpressung mit Fotos");
		event.setNoteAcct("Erpressungsgeld %N");
		event.setAmountMin(50000);
		event.setAmountMax(400000);
		event.setMaxpercent(15);
		event.setChance(20);
		result.add(event);

		event = new Event(1047, 6);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.PAYVAR_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N will Firmengeheimnisse an die Konkurrenz verraten, wenn Sie ihr nicht %A geben.");
		event.setNoteFile("Erpressung mit Firmeninternas");
		event.setNoteAcct("Erpressungsgeld %N");
		event.setAmountMin(10000);
		event.setAmountMax(200000);
		event.setMaxpercent(10);
		event.setChance(20);
		result.add(event);

		event = new Event(1048, 4);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N wird wegen Erregung öffentlichen Ärgernisses angezeigt. Für eine gemeinnützige Spende von %A wird die Anklage fallengelassen.");
		event.setNoteFile("Erregung öffentlichen Ärgernisses");
		event.setNoteAcct("Spende keine Klage gegen %N");
		event.setAmountMin(2000);
		event.setAmountMax(12000);
		event.setChance(30);
		result.add(event);

		event = new Event(1049, 3);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat Ihnen Bargeld in Höhe von %A gestohlen.");
		event.setNoteFile("Bargelddiebstahl %A");
		event.setNoteAcct("Bargelddiebstahl %N");
		event.setAmountMin(10000);
		event.setAmountMax(200000);
		event.setMaxpercent(15);
		event.setChance(40);
		result.add(event);

		event = new Event(1050, 4);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Der Kosmetikkoffer von %N ist am Flughafen verlorengegangen. Sie benötigt kurzfristig neue Utensilien für %A.");
		event.setNoteFile("Kosmetikkoffer verloren");
		event.setNoteAcct("Kosmetika für %N");
		event.setAmountMin(2000);
		event.setAmountMax(12000);
		event.setChance(15);
		result.add(event);

		event = new Event(1051, 4);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Der Kosmetikkoffer von %N ist bei einem Fototermin verlorengegangen. Sie benötigt kurzfristig neue Utensilien für %A.");
		event.setNoteFile("Kosmetikkoffer verloren");
		event.setNoteAcct("Kosmetika für %N");
		event.setAmountMin(2000);
		event.setAmountMax(12000);
		event.setChance(15);
		result.add(event);

		event = new Event(1052, 4);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Der Kosmetikkoffer von %N ist im Hotel verlorengegangen. Sie benötigt kurzfristig neue Utensilien für %A.");
		event.setNoteFile("Kosmetikkoffer verloren");
		event.setNoteAcct("Kosmetika für %N");
		event.setAmountMin(2000);
		event.setAmountMax(12000);
		event.setChance(15);
		result.add(event);

		event = new Event(1053, 4);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Der Kosmetikkoffer von %N wurde gestohlen. Sie benötigt kurzfristig neue Utensilien für %A.");
		event.setNoteFile("Kosmetikkoffer verloren");
		event.setNoteAcct("Kosmetika für %N");
		event.setAmountMin(2000);
		event.setAmountMax(12000);
		event.setChance(15);
		result.add(event);

		event = new Event(1054, 3);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat %A veruntreut.");
		event.setNoteFile("Veruntreuung %A");
		event.setNoteAcct("Veruntreut von %N");
		event.setAmountMin(20000);
		event.setAmountMax(400000);
		event.setMaxpercent(5);
		event.setChance(40);
		result.add(event);

		event = new Event(1055, 4);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Nach einer von %N organisierten Party in Ihrem Haus fallen für die Aufräumarbeiten Kosten in Höhe von %A an");
		event.setNoteFile("Chaotische Party gefeiert");
		event.setNoteAcct("Aufräumen nach Party für %N");
		event.setAmountMin(5000);
		event.setAmountMax(15000);
		event.setChance(30);
		result.add(event);

		event = new Event(1056, 4);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat sich ein Auto von Ihnen ausgeliehen und nicht wiedergebracht. Wiederbeschaffungswert: %A");
		event.setNoteFile("Auto entwendet");
		event.setNoteAcct("Auto verschwunden mit %N");
		event.setAmountMin(60000);
		event.setAmountMax(200000);
		event.setChance(30);
		result.add(event);

		event = new Event(1057, 3);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N ist nicht zum Fototermin erschienen. Der Fotograf verlangt Schadensersatz in Höhe von %A");
		event.setNoteFile("Fototermin verpaßt");
		event.setNoteAcct("Shooting mit %N verpaßt");
		event.setAmountMin(20000);
		event.setAmountMax(40000);
		event.setChance(50);
		result.add(event);

		event = new Event(1058, 6);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.PAYVAR_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat Sie beim Schnüffeln in ihrer Unterwäsche erwischt. Sie will Sie anzeigen, wenn Sie ihr nicht %A geben.");
		event.setNoteFile("Erpressung mit intimen Details %A");
		event.setNoteAcct("Erpressungsgeld %N");
		event.setAmountMin(10000);
		event.setAmountMax(200000);
		event.setMaxpercent(20);
		event.setChance(30);
		result.add(event);

		event = new Event(1059, 6);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.PAYVAR_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat Sie beim Spielen mit ihren Schuhen erwischt. Sie will Sie bloßstellen, wenn Sie ihr nicht %A geben.");
		event.setNoteFile("Erpressung mit intimen Details %A");
		event.setNoteAcct("Erpressungsgeld %N");
		event.setAmountMin(50000);
		event.setAmountMax(600000);
		event.setMaxpercent(20);
		event.setChance(40);
		result.add(event);

		event = new Event(1060, 4);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYVAR_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat sich beim Fotoshooting 2 Fingernägel gebrochen. Sie fordert %A Schmerzensgeld.");
		event.setNoteFile("Schmerzensgeld gebrochene Fingernägel");
		event.setNoteAcct("Schmerzensgeld %N");
		event.setAmountMin(1000);
		event.setAmountMax(4000);
		event.setMaxpercent(20);
		event.setChance(25);
		result.add(event);

		event = new Event(1061, 4);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYVAR_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat sich beim Fotoshooting gestoßen und einen blauen Fleck bekommen. Sie fordert %A Schmerzensgeld.");
		event.setNoteFile("Schmerzensgeld blauer Fleck");
		event.setNoteAcct("Schmerzensgeld %N");
		event.setAmountMin(2000);
		event.setAmountMax(8000);
		event.setMaxpercent(10);
		event.setChance(25);
		result.add(event);

		event = new Event(1062, 4);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYVAR_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat sich beim Fotoshooting den Knöchel verstaucht. Sie fordert %A Schmerzensgeld.");
		event.setNoteFile("Schmerzensgeld verstauchter Knöchel");
		event.setNoteAcct("Schmerzensgeld %N");
		event.setAmountMin(2000);
		event.setAmountMax(8000);
		event.setMaxpercent(10);
		event.setChance(25);
		result.add(event);

		event = new Event(1063, 4);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYVAR_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat sich beim Fotoshooting das Schienbein aufgekratzt. Sie fordert %A Schmerzensgeld.");
		event.setNoteFile("Schmerzensgeld Kratzwunde");
		event.setNoteAcct("Schmerzensgeld %N");
		event.setAmountMin(1000);
		event.setAmountMax(6000);
		event.setMaxpercent(10);
		event.setChance(25);
		result.add(event);

		event = new Event(1064, 7);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYOPT_PERSON);
		event.setIcon(EventIcon.MONEY);
		event.setDescription("%N möchte %A extra Taschengeld.");
		event.setNoteFile("Taschengeld %A");
		event.setNoteAcct("Taschengeld %N");
		event.setAmountMin(10000);
		event.setAmountMax(160000);
		event.setMaxpercent(50);
		event.setChance(50);
		result.add(event);

		event = new Event(1065, 5);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.PAYOPT_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat ihre eigene Entführung inszeniert. Sie verlangt %A Lösegeld");
		event.setNoteFile("Lösegeld %A");
		event.setNoteAcct("Lösegeld %N");
		event.setAmountMin(100000);
		event.setAmountMax(500000);
		event.setMaxpercent(25);
		event.setChance(50);
		result.add(event);

		event = new Event(1066, 3);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Die Spesenabrechnung von %N beläuft sich auf %A für Hotels, Flüge, Kosmetika, Kleidung und Schmuck.");
		event.setNoteFile("Spesenabrechnung");
		event.setNoteAcct("Spesenabrechnung %N");
		event.setAmountMin(10000);
		event.setAmountMax(75000);
		event.setMaxpercent(5);
		event.setChance(100);
		result.add(event);

		event = new Event(1067, 2);
		event.setEclass(EventClass.GAMBLE);
		event.setFlag(EventFlag.WIN);
		event.setIcon(EventIcon.LIGHTENING);
		event.setDescription("Glücksspiel.");
		event.setNoteFile("Glücksspiel");
		event.setNoteAcct("Glücksspiel");
		event.setAmountMin(1000);
		event.setAmountMax(10000000);
		event.setChance(100);
		result.add(event);

		event = new Event(1068, 3);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.CAR_WRECKED);
		event.setIcon(EventIcon.CAR);
		event.setDescription("%N hat ihr Auto (%C) zu Schrott gefahren. Um wieder zu Fototerminen anreisen zu können, braucht sie ein neues Auto.");
		result.add(event);

		event = new Event(SYSTEM_ID_CAR_ACCIDENT_COST, 2);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Nach einem von %N verursachten Verkehrsunfall auf dem Arbeitsweg belaufen sich die Kosten für Versicherungsselbstbeteiligung und Reparaturen auf %A.");
		event.setNoteFile("Verkehrsunfall");
		event.setNoteAcct("Selbstbeteiligung und Reparaturen nach Verkehrsunfall von %N");
		event.setAmountMin(5000);
		event.setAmountMax(60000);
		event.setChance(0);	// can only be picked explicitely
		result.add(event);

		event = new Event(1070, 6);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.GROUPWORK);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Das Team %T hat heute Einnahmen in Höhe von %A erwirtschaftet. Die Leiterin %N erhält dafür %B als Bonus.");
		event.setNoteFile("Das Team %T hat heute Einnahmen in Höhe von %A erwirtschaftet. Die Leiterin %N erhält dafür %B als Bonus.");
		event.setNoteAcct("Prämie für Koordination der Aufträge im Team %T");
		result.add(event);

		event = new Event(1071, 1);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.TRAINING_FIN);
		event.setIcon(EventIcon.EDUCATION);
		event.setDescription("%N hat ihre Fortbildung abgeschlossen.");
		result.add(event);

		event = new Event(1072, 1);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.NO_GROUPWORK);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Im Team %T konnten heute keine Aufträge abgearbeitet werden, da die Teamleiterin und ihre Stellvertreterin nicht verfügbar sind.");
		result.add(event);

		event = new Event(1073, 1);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYOPT_PERSON);
		event.setIcon(EventIcon.MONEY);
		event.setDescription("%N möchte einen Zuschuss für einen Immobilienkauf in Höhe von %A.");
		event.setNoteFile("Immobilienkaufzuschuss %A");
		event.setNoteAcct("Immobilienkaufzuschuss %N");
		event.setAmountMin(100000);
		event.setAmountMax(300000);
		event.setMaxpercent(10);
		event.setChance(20);
		result.add(event);

		event = new Event(1074, 2);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat Ihren Bankzugang gehackt und online %A von Ihrem Konto abgebucht.");
		event.setNoteFile("Onlinediebstahl %A");
		event.setNoteAcct("Onlinediebstahl %N");
		event.setAmountMin(10000);
		event.setAmountMax(200000);
		event.setMaxpercent(15);
		event.setChance(40);
		result.add(event);

		event = new Event(1075, 1);
		event.setEclass(EventClass.BOOKREJECT);
		event.setFlag(EventFlag.PHOTO);
		event.setIcon(EventIcon.CAMERA);
		event.setDescription("%N kann nicht an dem Fotoshooting mitarbeiten, für das sie für %A gebucht wurde.");
		event.setNoteFile("Absage Fotoshooting, Gage %A");
		result.add(event);

		event = new Event(1076, 1);
		event.setEclass(EventClass.BOOKREJECT);
		event.setFlag(EventFlag.MOVIE);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("%N kann nicht an den Dreharbeiten des Films mitarbeiten, für die sie für %A gebucht wurde.");
		event.setNoteFile("Absage Dreharbeiten, Gage %A");
		result.add(event);

		event = new Event(1077, 1);
		event.setEclass(EventClass.MOVIE_START);
		event.setFlag(EventFlag.MOVIE_ENTERTAIN);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("Vorbereitung der Dreharbeiten zum Film %M.");
		event.setAmountMin(180000);
		event.setAmountMax(250000);
		result.add(event);

		event = new Event(1078, 1);
		event.setEclass(EventClass.MOVIE_START);
		event.setFlag(EventFlag.MOVIE_EROTIC);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("Vorbereitung der Dreharbeiten zum Film %M.");
		event.setAmountMin(120000);
		event.setAmountMax(180000);
		result.add(event);

		event = new Event(1079, 1);
		event.setEclass(EventClass.MOVIE_START);
		event.setFlag(EventFlag.MOVIE_PORN);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("Vorbereitung der Dreharbeiten zum Film %M.");
		event.setAmountMin(80000);
		event.setAmountMax(130000);
		result.add(event);

		event = new Event(1080, 2);
		event.setEclass(EventClass.MOVIE_PROGRESS);
		event.setFlag(EventFlag.MOVIE_ENTERTAIN);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("Dreharbeiten zum Film %M.");
		event.setAmountMin(30000);
		event.setAmountMax(50000);
		result.add(event);

		event = new Event(1081, 2);
		event.setEclass(EventClass.MOVIE_PROGRESS);
		event.setFlag(EventFlag.MOVIE_EROTIC);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("Dreharbeiten zum Film %M.");
		event.setAmountMin(20000);
		event.setAmountMax(40000);
		result.add(event);

		event = new Event(1082, 2);
		event.setEclass(EventClass.MOVIE_PROGRESS);
		event.setFlag(EventFlag.MOVIE_PORN);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("Dreharbeiten zum Film %M.");
		event.setAmountMin(10000);
		event.setAmountMax(30000);
		result.add(event);

		event = new Event(1083, 1);
		event.setEclass(EventClass.MOVIE_FINISH);
		event.setFlag(EventFlag.MOVIE_ENTERTAIN);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("Abschluß der Dreharbeiten zum Film %M.");
		event.setAmountMin(180000);
		event.setAmountMax(250000);
		result.add(event);

		event = new Event(1084, 1);
		event.setEclass(EventClass.MOVIE_FINISH);
		event.setFlag(EventFlag.MOVIE_EROTIC);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("Abschluß der Dreharbeiten zum Film %M.");
		event.setAmountMin(120000);
		event.setAmountMax(180000);
		result.add(event);

		event = new Event(1085, 1);
		event.setEclass(EventClass.MOVIE_FINISH);
		event.setFlag(EventFlag.MOVIE_PORN);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("Abschluß der Dreharbeiten zum Film %M.");
		event.setAmountMin(80000);
		event.setAmountMax(130000);
		result.add(event);

		event = new Event(1086, 1);
		event.setEclass(EventClass.MOVIE_CAST);
		event.setFlag(EventFlag.MOVIE);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("Mitarbeit an den Dreharbeiten zum Film %M.");
		event.setAmountMin(1000);
		result.add(event);

		event = new Event(1087, 2);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.BUSTED);
		event.setIcon(EventIcon.LIGHTENING);
		event.setDescription("Ihr Konto ist um %A überzogen. Nehmen Sie bei einem Model einen Kredit auf, um weiterarbeiten zu können.");
		result.add(event);

		return result;
	}
}
