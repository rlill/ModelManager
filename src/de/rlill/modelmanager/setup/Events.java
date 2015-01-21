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
		event.setDescription("Tagesabschlu�.");
		result.add(event);

		event = new Event(1001, 2);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.PAYCHECK);
		event.setIcon(EventIcon.MONEY);
		event.setDescription("%N hat heute ihr Gehalt in H�he von %A bekommen.");
		event.setNoteAcct("Gehalt");
		result.add(event);

		event = new Event(1002, 1);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.QUIT);
		event.setIcon(EventIcon.DOOR);
		event.setDescription("%N hat gek�ndigt.");
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
		event.setDescription("%N hat einen Blechschaden mit ihrem Auto (%C) verursacht. Es mu� instandgesetzt werden.");
		result.add(event);

		event = new Event(1005, 3);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.CAR_STOLEN);
		event.setIcon(EventIcon.CAR);
		event.setDescription("%N wurde ihr Auto (%C) gestohlen. Um wieder f�r Fototermine gebucht werden zu k�nnen, braucht sie ein neues Auto.");
		result.add(event);

		event = new Event(1006, 2);
		event.setEclass(EventClass.BOOKING);
		event.setFlag(EventFlag.PHOTO);
		event.setIcon(EventIcon.CAMERA);
		event.setDescription("%N soll heute f�r %A f�r ein Fotoshooting gebucht werden.");
		event.setNoteFile("Gebucht f�r Fotoshooting, Gage %A");
		event.setNoteAcct("Gage f�r Fotoshooting %N");
		event.setAmountMin(1000);
		event.setAmountMax(2000);
		event.setChance(100);
		result.add(event);

		event = new Event(1007, 5);
		event.setEclass(EventClass.BOOKING);
		event.setFlag(EventFlag.MOVIE);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("%N soll heute f�r %A f�r den Dreh eines Films gebucht werden.");
		event.setNoteFile("Gebucht f�r Dreharbeiten, Gage %A");
		event.setNoteAcct("Gage f�r Dreharbeiten %N");
		event.setAmountMin(1500);
		event.setAmountMax(3000);
		result.add(event);

		event = new Event(1008, 2);
		event.setEclass(EventClass.REQUEST);
		event.setFlag(EventFlag.RAISE);
		event.setIcon(EventIcon.MONEY);
		event.setDescription("%N m�chte eine Gehaltserh�hung von %S auf %A pro Woche.");
		event.setNoteFile("Gehaltserh�hung von %S auf %A");
		result.add(event);

		event = new Event(1009, 2);
		event.setEclass(EventClass.REQUEST);
		event.setFlag(EventFlag.BONUS);
		event.setIcon(EventIcon.MONEY);
		event.setDescription("%N fordert eine Pr�mie in H�he von %A.");
		event.setNoteFile("Pr�mie %A");
		event.setNoteAcct("Pr�mie %N");
		result.add(event);

		event = new Event(1010, 1);
		event.setEclass(EventClass.REQUEST);
		event.setFlag(EventFlag.QUIT);
		event.setIcon(EventIcon.DOOR);
		event.setDescription("%N will k�ndigen.");
		event.setNoteFile("K�ndigung");
		result.add(event);

		event = new Event(1011, 2);
		event.setEclass(EventClass.REQUEST);
		event.setFlag(EventFlag.VACATION);
		event.setIcon(EventIcon.PALMTREE);
		event.setDescription("%N m�chte Urlaub nehmen. Sie hat %V Tage Resturlaub.");
		event.setNoteFile("Urlaub");
		result.add(event);

		event = new Event(1012, 1);
		event.setEclass(EventClass.REQUEST);
		event.setFlag(EventFlag.TRAINING);
		event.setIcon(EventIcon.EDUCATION);
		event.setDescription("%N m�chte eine Fortbildung besuchen.");
		event.setNoteFile("Fortbildung");
		result.add(event);

		event = new Event(1013, 3);
		event.setEclass(EventClass.REQUEST);
		event.setFlag(EventFlag.CAR_UPDATE);
		event.setIcon(EventIcon.CAR);
		event.setDescription("%N m�chte ein neues Auto. Derzeitiges Fahrzeug: %C");
		event.setNoteFile("Neues Auto");
		result.add(event);

		event = new Event(1014, 3);
		event.setEclass(EventClass.APPLICATION);
		event.setFlag(EventFlag.HIRE);
		event.setIcon(EventIcon.CONTRACT);
		event.setDescription("%N bewirbt sich um eine Anstellung. Sie m�chte %A Gehalt pro Woche.");
		event.setNoteFile("Bewerbung");
		event.setAmountMin(1000);
		result.add(event);

		event = new Event(1015, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Bei der Beseitigung der Leiche eines Drogendealers fallen Ihnen %A in die H�nde.");
		event.setNoteAcct("Bareinzahlung");
		event.setAmountMin(50000);
		event.setAmountMax(250000);
		event.setChance(40);
		result.add(event);

		event = new Event(1016, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Nach einer Party in Ihrem Haus bleibt ein anonymes Geldb�ndel von %A liegen.");
		event.setNoteAcct("Bareinzahlung");
		event.setAmountMin(50000);
		event.setAmountMax(250000);
		event.setChance(30);
		result.add(event);

		event = new Event(1017, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("F�r einen guten Bekannten gibt %N eine Sondervorstellung. Dieser bezahlt daf�r %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage f�r Sondervorstellung mit %N");
		event.setAmountMin(10000);
		event.setAmountMax(40000);
		event.setChance(60);
		result.add(event);

		event = new Event(1018, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("F�r einen wichtigen Politiker gibt %N eine Sondervorstellung. Dieser bezahlt daf�r %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage f�r Sondervorstellung mit %N");
		event.setAmountMin(20000);
		event.setAmountMax(60000);
		event.setChance(50);
		result.add(event);

		event = new Event(1019, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N gibt f�r einen Wirtschaftsboss eine Sondervorstellung. Dieser bezahlt daf�r %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage f�r Sondervorstellung mit %N");
		event.setAmountMin(10000);
		event.setAmountMax(40000);
		event.setChance(50);
		result.add(event);

		event = new Event(1020, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N gibt f�r einen B�rsenmakler eine Sondervorstellung. Dieser bezahlt daf�r %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage f�r Sondervorstellung mit %N");
		event.setAmountMin(20000);
		event.setAmountMax(60000);
		event.setChance(60);
		result.add(event);

		event = new Event(1021, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N wird Model des Jahres in einem M�nnermagazin. Als Manager erhalten Sie einen Anteil des Preises in H�he von %A");
		event.setNoteFile("Preiskr�nung Model des Jahres");
		event.setNoteAcct("Preisgeldanteil %N (Model des Jahres)");
		event.setAmountMin(2000);
		event.setAmountMax(10000);
		event.setChance(50);
		result.add(event);

		event = new Event(1022, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N begleitet einen reichen Kunden auf einen Ball. Dieser bezahlt daf�r %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage f�r Begleitservice durch %N");
		event.setAmountMin(5000);
		event.setAmountMax(15000);
		event.setChance(80);
		result.add(event);

		event = new Event(1023, 3);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N begleitet einen Scheich zum Golfen. Dieser bezahlt daf�r %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage f�r Begleitservice durch %N");
		event.setAmountMin(25000);
		event.setAmountMax(80000);
		event.setChance(60);
		result.add(event);

		event = new Event(1024, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N begleitet einen Million�r ins Theater. Dieser bezahlt daf�r %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage f�r Begleitservice durch %N");
		event.setAmountMin(10000);
		event.setAmountMax(25000);
		event.setChance(80);
		result.add(event);

		event = new Event(1025, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N begleitet einen B�rsenmakler auf einen Ball. Dieser bezahlt daf�r %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage f�r Begleitservice durch %N");
		event.setAmountMin(5000);
		event.setAmountMax(15000);
		event.setChance(70);
		result.add(event);

		event = new Event(1026, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N begleitet einen Wirtschaftsboss zum Golfen. Dieser bezahlt daf�r %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage f�r Begleitservice durch %N");
		event.setAmountMin(4000);
		event.setAmountMax(8000);
		event.setChance(70);
		result.add(event);

		event = new Event(1027, 3);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N begleitet einen Scheich ins Theater. Dieser bezahlt daf�r %A");
		event.setNoteFile("Sondervorstellung, Gage %A");
		event.setNoteAcct("Gage f�r Begleitservice durch %N");
		event.setAmountMin(25000);
		event.setAmountMax(80000);
		event.setChance(50);
		result.add(event);

		event = new Event(1028, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Daf�r, da� Sie mit einer Gruppe Models auf seiner Party f�r Stimmung gesorgt haben, gibt Ihnen ein Bekannter %A");
		event.setNoteAcct("Partyservice");
		event.setAmountMin(5000);
		event.setAmountMax(25000);
		event.setChance(60);
		result.add(event);

		event = new Event(1029, 2);
		event.setEclass(EventClass.EXTRA_IN);
		event.setFlag(EventFlag.WIN);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Sie lassen einen Kumpel eine Party in Ihrem Haus schmei�en. Er gibt ihnen daf�r %A");
		event.setNoteAcct("Partyservice");
		event.setAmountMin(5000);
		event.setAmountMax(25000);
		event.setChance(50);
		result.add(event);

		event = new Event(1030, 2);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Eine neugierige Amtsperson stellt zu viele Fragen. F�r einen Obulus von %A gibt sie sich zufrieden.");
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
		event.setDescription("Sie werden wegen Steuerhinterziehung angeklagt. Ihr Anwalt hilft Ihnen, Ihre Unschuld zu beweisen und will %A daf�r.");
		event.setNoteAcct("Anwaltskosten Steuerhinterziehungsklage");
		event.setAmountMin(5000);
		event.setAmountMax(10000);
		event.setChance(15);
		result.add(event);

		event = new Event(1032, 2);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Sie werden wegen Urheberrechtsverletzung angeklagt. Ihr Anwalt hilft Ihnen, Ihre Unschuld zu beweisen und will %A daf�r.");
		event.setNoteAcct("Anwaltskosten Urheberrechtsklage");
		event.setAmountMin(4000);
		event.setAmountMax(8000);
		event.setChance(15);
		result.add(event);

		event = new Event(1033, 2);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Wegen eines Versto�es gegen die Wettbewerbsregeln m�ssen Sie einem Konkurrenten %A Schadensersatz leisten.");
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
		event.setDescription("%N hat bei einem Fotoshooting mutwillig eine Kamera besch�digt. Die Reparatur kostet %A.");
		event.setNoteFile("Mutwillig Kamera besch�digt");
		event.setNoteAcct("Schadensersatz Kamera besch�digt durch %N");
		event.setAmountMin(12000);
		event.setAmountMax(33000);
		event.setChance(25);
		result.add(event);

		event = new Event(1035, 3);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Bei einem Fotoshooting hat %N mutwillig mehrere Scheinwerfer besch�digt. Der Preis f�r den Ersatz bel�uft sich auf %A.");
		event.setNoteFile("Mutwillig Scheinwerfer besch�digt");
		event.setNoteAcct("Schadensersatz Scheinwerfer besch�digt durch %N");
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
		event.setDescription("%N hat den Studioboden mit ihren Schuhen ruiniert. Es mu� f�r %A neuer Parkett verlegt werden.");
		event.setNoteFile("Studioeinrichtung besch�digt");
		event.setNoteAcct("Schadensersatz Studioboden besch�digt durch %N");
		event.setAmountMin(12000);
		event.setAmountMax(60000);
		event.setChance(50);
		result.add(event);

		event = new Event(1039, 3);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Bei einem Fotoshooting hat %N ihre Designerschuhe ruiniert. Sie m�ssen sie f�r %A ersetzen.");
		event.setNoteFile("Schuhe besch�digt");
		event.setNoteAcct("Schadensersatz Schuhe %N");
		event.setAmountMin(10000);
		event.setAmountMax(20000);
		event.setChance(50);
		result.add(event);

		event = new Event(1040, 3);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Bei einem Fotoshooting hat %N ihr Kleid ruiniert. Sie m�ssen es f�r %A ersetzen.");
		event.setNoteFile("Kleid besch�digt");
		event.setNoteAcct("Schadensersatz Kleid %N");
		event.setAmountMin(5000);
		event.setAmountMax(15000);
		event.setChance(25);
		result.add(event);

		event = new Event(1041, 3);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Bei einem Fotoshooting hat %N ihren Badeanzug ruiniert. Sie m�ssen ihn f�r %A ersetzen.");
		event.setNoteFile("Badeanzug besch�digt");
		event.setNoteAcct("Schadensersatz Badenazug %N");
		event.setAmountMin(500);
		event.setAmountMax(2000);
		event.setChance(25);
		result.add(event);

		event = new Event(1042, 3);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Bei einem Fotoshooting hat %N ihre Handtasche ruiniert. Sie m�ssen sie f�r %A ersetzen.");
		event.setNoteFile("Handtasche besch�digt");
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
		event.setDescription("%N wird wegen Versto�es gegen das Bet�ubungsmittelgesetz angeklagt. Ihr Anwalt holt sie f�r %A da raus.");
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
		event.setDescription("%N will peinliche Fotos von Ihnen ver�ffentlichen, wenn Sie ihr nicht %A geben.");
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
		event.setDescription("%N wird wegen Erregung �ffentlichen �rgernisses angezeigt. F�r eine gemeinn�tzige Spende von %A wird die Anklage fallengelassen.");
		event.setNoteFile("Erregung �ffentlichen �rgernisses");
		event.setNoteAcct("Spende keine Klage gegen %N");
		event.setAmountMin(2000);
		event.setAmountMax(12000);
		event.setChance(30);
		result.add(event);

		event = new Event(1049, 3);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat Ihnen Bargeld in H�he von %A gestohlen.");
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
		event.setDescription("Der Kosmetikkoffer von %N ist am Flughafen verlorengegangen. Sie ben�tigt kurzfristig neue Utensilien f�r %A.");
		event.setNoteFile("Kosmetikkoffer verloren");
		event.setNoteAcct("Kosmetika f�r %N");
		event.setAmountMin(2000);
		event.setAmountMax(12000);
		event.setChance(15);
		result.add(event);

		event = new Event(1051, 4);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Der Kosmetikkoffer von %N ist bei einem Fototermin verlorengegangen. Sie ben�tigt kurzfristig neue Utensilien f�r %A.");
		event.setNoteFile("Kosmetikkoffer verloren");
		event.setNoteAcct("Kosmetika f�r %N");
		event.setAmountMin(2000);
		event.setAmountMax(12000);
		event.setChance(15);
		result.add(event);

		event = new Event(1052, 4);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Der Kosmetikkoffer von %N ist im Hotel verlorengegangen. Sie ben�tigt kurzfristig neue Utensilien f�r %A.");
		event.setNoteFile("Kosmetikkoffer verloren");
		event.setNoteAcct("Kosmetika f�r %N");
		event.setAmountMin(2000);
		event.setAmountMax(12000);
		event.setChance(15);
		result.add(event);

		event = new Event(1053, 4);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Der Kosmetikkoffer von %N wurde gestohlen. Sie ben�tigt kurzfristig neue Utensilien f�r %A.");
		event.setNoteFile("Kosmetikkoffer verloren");
		event.setNoteAcct("Kosmetika f�r %N");
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
		event.setDescription("Nach einer von %N organisierten Party in Ihrem Haus fallen f�r die Aufr�umarbeiten Kosten in H�he von %A an");
		event.setNoteFile("Chaotische Party gefeiert");
		event.setNoteAcct("Aufr�umen nach Party f�r %N");
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
		event.setDescription("%N ist nicht zum Fototermin erschienen. Der Fotograf verlangt Schadensersatz in H�he von %A");
		event.setNoteFile("Fototermin verpa�t");
		event.setNoteAcct("Shooting mit %N verpa�t");
		event.setAmountMin(20000);
		event.setAmountMax(40000);
		event.setChance(50);
		result.add(event);

		event = new Event(1058, 6);
		event.setEclass(EventClass.EXTRA_LOSS);
		event.setFlag(EventFlag.PAYVAR_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("%N hat Sie beim Schn�ffeln in ihrer Unterw�sche erwischt. Sie will Sie anzeigen, wenn Sie ihr nicht %A geben.");
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
		event.setDescription("%N hat Sie beim Spielen mit ihren Schuhen erwischt. Sie will Sie blo�stellen, wenn Sie ihr nicht %A geben.");
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
		event.setDescription("%N hat sich beim Fotoshooting 2 Fingern�gel gebrochen. Sie fordert %A Schmerzensgeld.");
		event.setNoteFile("Schmerzensgeld gebrochene Fingern�gel");
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
		event.setDescription("%N hat sich beim Fotoshooting gesto�en und einen blauen Fleck bekommen. Sie fordert %A Schmerzensgeld.");
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
		event.setDescription("%N hat sich beim Fotoshooting den Kn�chel verstaucht. Sie fordert %A Schmerzensgeld.");
		event.setNoteFile("Schmerzensgeld verstauchter Kn�chel");
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
		event.setDescription("%N m�chte %A extra Taschengeld.");
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
		event.setDescription("%N hat ihre eigene Entf�hrung inszeniert. Sie verlangt %A L�segeld");
		event.setNoteFile("L�segeld %A");
		event.setNoteAcct("L�segeld %N");
		event.setAmountMin(100000);
		event.setAmountMax(500000);
		event.setMaxpercent(25);
		event.setChance(50);
		result.add(event);

		event = new Event(1066, 3);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYFIX_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Die Spesenabrechnung von %N bel�uft sich auf %A f�r Hotels, Fl�ge, Kosmetika, Kleidung und Schmuck.");
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
		event.setDescription("Gl�cksspiel.");
		event.setNoteFile("Gl�cksspiel");
		event.setNoteAcct("Gl�cksspiel");
		event.setAmountMin(1000);
		event.setAmountMax(10000000);
		event.setChance(100);
		result.add(event);

		event = new Event(1068, 3);
		event.setEclass(EventClass.NOTIFICATION);
		event.setFlag(EventFlag.CAR_WRECKED);
		event.setIcon(EventIcon.CAR);
		event.setDescription("%N hat ihr Auto (%C) zu Schrott gefahren. Um wieder zu Fototerminen anreisen zu k�nnen, braucht sie ein neues Auto.");
		result.add(event);

		event = new Event(SYSTEM_ID_CAR_ACCIDENT_COST, 2);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.LOSE_PERSON);
		event.setIcon(EventIcon.ARROW);
		event.setDescription("Nach einem von %N verursachten Verkehrsunfall auf dem Arbeitsweg belaufen sich die Kosten f�r Versicherungsselbstbeteiligung und Reparaturen auf %A.");
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
		event.setDescription("Das Team %T hat heute Einnahmen in H�he von %A erwirtschaftet. Die Leiterin %N erh�lt daf�r %B als Bonus.");
		event.setNoteFile("Das Team %T hat heute Einnahmen in H�he von %A erwirtschaftet. Die Leiterin %N erh�lt daf�r %B als Bonus.");
		event.setNoteAcct("Pr�mie f�r Koordination der Auftr�ge im Team %T");
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
		event.setDescription("Im Team %T konnten heute keine Auftr�ge abgearbeitet werden, da die Teamleiterin und ihre Stellvertreterin nicht verf�gbar sind.");
		result.add(event);

		event = new Event(1073, 1);
		event.setEclass(EventClass.EXTRA_OUT);
		event.setFlag(EventFlag.PAYOPT_PERSON);
		event.setIcon(EventIcon.MONEY);
		event.setDescription("%N m�chte einen Zuschuss f�r einen Immobilienkauf in H�he von %A.");
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
		event.setDescription("%N kann nicht an dem Fotoshooting mitarbeiten, f�r das sie f�r %A gebucht wurde.");
		event.setNoteFile("Absage Fotoshooting, Gage %A");
		result.add(event);

		event = new Event(1076, 1);
		event.setEclass(EventClass.BOOKREJECT);
		event.setFlag(EventFlag.MOVIE);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("%N kann nicht an den Dreharbeiten des Films mitarbeiten, f�r die sie f�r %A gebucht wurde.");
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
		event.setDescription("Abschlu� der Dreharbeiten zum Film %M.");
		event.setAmountMin(180000);
		event.setAmountMax(250000);
		result.add(event);

		event = new Event(1084, 1);
		event.setEclass(EventClass.MOVIE_FINISH);
		event.setFlag(EventFlag.MOVIE_EROTIC);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("Abschlu� der Dreharbeiten zum Film %M.");
		event.setAmountMin(120000);
		event.setAmountMax(180000);
		result.add(event);

		event = new Event(1085, 1);
		event.setEclass(EventClass.MOVIE_FINISH);
		event.setFlag(EventFlag.MOVIE_PORN);
		event.setIcon(EventIcon.MOVIECAM);
		event.setDescription("Abschlu� der Dreharbeiten zum Film %M.");
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
		event.setDescription("Ihr Konto ist um %A �berzogen. Nehmen Sie bei einem Model einen Kredit auf, um weiterarbeiten zu k�nnen.");
		result.add(event);

		return result;
	}
}
