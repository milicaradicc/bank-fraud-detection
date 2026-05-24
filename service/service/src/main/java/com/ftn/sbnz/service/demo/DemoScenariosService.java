package com.ftn.sbnz.service.demo;

import com.ftn.sbnz.model.enums.ClientSegment;
import com.ftn.sbnz.model.enums.TransactionChannel;
import com.ftn.sbnz.model.events.AccountChange;
import com.ftn.sbnz.model.events.LoginEvent;
import com.ftn.sbnz.model.events.Transaction;
import com.ftn.sbnz.model.events.UserBehavior;
import com.ftn.sbnz.model.facts.Alert;
import com.ftn.sbnz.model.facts.Client;
import com.ftn.sbnz.model.facts.ConfigList;
import com.ftn.sbnz.model.facts.Flag;
import com.ftn.sbnz.model.facts.RiskScore;
import com.ftn.sbnz.service.demo.ScenarioResult;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Demo scenariji za odbranu projekta.
 *
 * Sadrzi 10 scenarija koji zajedno aktiviraju SVA pravila u sistemu:
 *  1. Marija - Account Takeover (CEP failed burst, suspicious login, L2 chain)
 *  2. Money Mule - pass-through aktivnost, muling pattern
 *  3. APP Scam - autorizovana prevara, osetljiv segment
 *  4. Card Testing - velocity attack, VIP umanjenje
 *  5. Suspicious Profile - sva profil pravila
 *  6. Night Gambling - neuobicajeno vreme + rizican merchant
 *  7. Impossible Travel - CEP geografska detekcija
 *  8. Structuring - smurfing + AML alarm
 *  9. Happy Path - normalna transakcija prolazi
 * 10. Cumulative Escalation - watchlist boost + kumulativna eskalacija
 *
 * Svaki scenario koristi pseudo clock da bi mogli da simuliramo CEP
 * vremenske prozore (5 min za failed login burst, 48h za structuring).
 */
@Service
public class DemoScenariosService {

    private static final Logger log = LoggerFactory.getLogger(DemoScenariosService.class);
    private static final SimpleDateFormat TIME_FMT = new SimpleDateFormat("HH:mm:ss");

    private final KieContainer kieContainer;

    @Autowired
    public DemoScenariosService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    // =====================================================================
    // SCENARIO 1: Marija - Account Takeover
    //
    // Aktivira pravila:
    //   CEP: Failed login burst (5+ neuspeha u 5m) -> BRUTE_FORCE_POKUSAJ
    //   CEP: Failed burst + new device -> SuspiciousLogin event + SUSPICIOUS_LOGIN flag
    //   L1: Abnormalno visok iznos (4800 > 5x250)
    //   L1: Nepoznat uredjaj
    //   L1: Prvi transfer ka novom primaocu
    //   L2: Sumnjiv login + izmena kontakta
    //   L2: Sumnjiv login + novi primalac
    //   L2: Burst promena na nalogu (2+ promene u 60m)
    //   L3: Bazni skor + multiplikator 3+ kriticnih (x1.2)
    //   L4: Kriticni rizik -> FREEZE_ACCOUNT
    // =====================================================================
    public ScenarioResult runMarijaScenario() {
        ScenarioResult result = new ScenarioResult(
                "1. Marija - Account Takeover",
                "Napadac kompromituje pristupne podatke, prijavljuje se sa novog "
                        + "uredjaja, menja email, dodaje novog primaoca i pokusava transfer "
                        + "od 4800 EUR. Demonstrira CEP, ulancavanje L1->L2->L3->L4, "
                        + "i multiplikatorska pravila."
        );

        KieSession session = kieContainer.newKieSession("fraudPseudoSession");
        SessionPseudoClock clock = session.getSessionClock();

        try {
            long t0 = System.currentTimeMillis();
            clock.advanceTime(t0, TimeUnit.MILLISECONDS);

            Client marija = new Client("MARIJA_001", "Marija Radic", 45,
                    ClientSegment.REDOVNI, LocalDate.now().minusYears(5),
                    "RS", 2500.0, 250.0);
            marija.getKnownDevices().add("Marijin-Telefon");
            marija.getKnownDevices().add("Marijin-Laptop");
            marija.getKnownRecipients().add("Stari-Primalac");
            marija.setHistoricalOutflowInflowRatio(0.25);

            session.insert(marija);
            session.insert(defaultConfig());
            session.fireAllRules();

            result.addStep(formatTime(t0), "KORAK 1",
                    "5 neuspesnih prijava sa IP iz Holandije u 3 min");

            for (int i = 1; i <= 5; i++) {
                LoginEvent failed = new LoginEvent("fail_" + i,
                        marija.getClientId(), new Date(t0 + (i - 1) * 30_000L),
                        false, "Napadac-Uredjaj");
                failed.setCountry("NL");
                failed.setLatitude(52.3676);
                failed.setLongitude(4.9041);
                failed.setKnownDevice(false);
                session.insert(failed);
                if (i < 5) clock.advanceTime(30, TimeUnit.SECONDS);
            }
            session.fireAllRules();

            clock.advanceTime(60, TimeUnit.SECONDS);
            long t2 = t0 + 4 * 60_000L;
            result.addStep(formatTime(t2), "KORAK 2",
                    "Uspesna prijava sa nepoznatog uredjaja iz Holandije");

            LoginEvent success = new LoginEvent("success",
                    marija.getClientId(), new Date(t2), true, "Napadac-Uredjaj");
            success.setCountry("NL");
            success.setLatitude(52.3676);
            success.setLongitude(4.9041);
            success.setKnownDevice(false);
            session.insert(success);
            session.fireAllRules();

            clock.advanceTime(3, TimeUnit.MINUTES);
            long t3 = t0 + 7 * 60_000L;
            result.addStep(formatTime(t3), "KORAK 3",
                    "Napadac menja email adresu za notifikacije");

            AccountChange emailChange = new AccountChange("ch_email",
                    marija.getClientId(), new Date(t3),
                    AccountChange.ChangeType.EMAIL_CHANGE);
            emailChange.setNewValue("napadac@evil.com");
            session.insert(emailChange);
            session.fireAllRules();

            clock.advanceTime(4, TimeUnit.MINUTES);
            long t4 = t0 + 11 * 60_000L;
            result.addStep(formatTime(t4), "KORAK 4",
                    "Napadac dodaje novog primaoca - racun u inostranoj banci");

            AccountChange newRecipient = new AccountChange("ch_recipient",
                    marija.getClientId(), new Date(t4),
                    AccountChange.ChangeType.NEW_RECIPIENT);
            newRecipient.setNewValue("Petar-P-Inostrani");
            session.insert(newRecipient);
            session.fireAllRules();

            clock.advanceTime(2, TimeUnit.MINUTES);
            long t5 = t0 + 13 * 60_000L;
            result.addStep(formatTime(t5), "KORAK 5",
                    "Pokusaj transfera 4800 EUR (19.2x prosek!)");

            Transaction transfer = new Transaction("tx_marija",
                    marija.getClientId(), 4800.0, new Date(t5),
                    TransactionChannel.TRANSFER);
            transfer.setCurrency("EUR");
            transfer.setRecipientId("Petar-P-Inostrani");
            transfer.setRecipientCountry("NL");
            transfer.setRecipientIsForeignAccount(true);
            transfer.setDeviceId("Napadac-Uredjaj");
            transfer.setCountry("NL");
            transfer.setLatitude(52.3676);
            transfer.setLongitude(4.9041);
            transfer.setInflow(false);
            session.insert(transfer);
            session.fireAllRules();

            return finalizeResult(result, session, marija.getClientId(), "tx_marija");
        } finally {
            session.dispose();
        }
    }

    // =====================================================================
    // SCENARIO 2: Money Mule
    //
    // Aktivira pravila:
    //   L2: Sumnjiva aktivacija (90+ dana neaktivan + L1 flag)
    //   L2: Neocekivani priliv (>50% mesecnog proseka)
    //   L2: Pass-through aktivnost (odliv/priliv > 0.9, istorijski < 0.3)
    //   L2: Muling obrazac (10+ razlicitih primalaca, prosek < 200 EUR)
    //   L2: Vise sumnjivih primalaca (3+ novootvorenih/inostranih u 48h)
    //   CEP: Burst nakon mirovanja (90+ dana neaktivan + 3+ tx u 24h)
    //   L1: Rizicna lokacija (AF primaoci)
    //   L1: Rizican primalac
    //   L4: Kriticni rizik + AML prijava
    // =====================================================================
    public ScenarioResult runMoneyMuleScenario() {
        ScenarioResult result = new ScenarioResult(
                "2. Money Mule - Pass-through nalog",
                "Klijent (regrutovan kao mula) prima velik priliv i u 24h ga "
                        + "prosledjuje kroz mnogo malih transfera ka inostranim primaocima. "
                        + "Demonstrira accumulate funkciju (collectSet, sum) i AML detekciju."
        );

        KieSession session = kieContainer.newKieSession("fraudPseudoSession");
        SessionPseudoClock clock = session.getSessionClock();

        try {
            long t0 = System.currentTimeMillis();
            clock.advanceTime(t0, TimeUnit.MILLISECONDS);

            Client stefan = new Client("STEFAN_002", "Stefan M.", 28,
                    ClientSegment.REDOVNI, LocalDate.now().minusMonths(4),
                    "RS", 1500.0, 150.0);
            stefan.setDaysInactiveBeforeReactivation(95);
            stefan.setHistoricalOutflowInflowRatio(0.20);

            session.insert(stefan);
            session.insert(defaultConfig());
            session.fireAllRules();

            result.addStep(formatTime(t0), "KORAK 1",
                    "Priliv 10000 EUR na dugo neaktivan nalog");

            Transaction inflow = new Transaction("tx_inflow",
                    stefan.getClientId(), 10000.0, new Date(t0),
                    TransactionChannel.TRANSFER);
            inflow.setCurrency("EUR");
            inflow.setInflow(true);
            inflow.setRecipientId("nepoznat-uplatilac");
            session.insert(inflow);
            session.fireAllRules();

            result.addStep(formatTime(t0 + 60_000L), "KORAK 2",
                    "Start prosledjivanja - 12 malih transfera ka razlicitim primaocima");

            for (int i = 1; i <= 12; i++) {
                clock.advanceTime(20, TimeUnit.MINUTES);
                long ti = t0 + (i * 20L * 60_000L);

                Transaction outflow = new Transaction("tx_out_" + i,
                        stefan.getClientId(), 150.0 + (i * 5),
                        new Date(ti), TransactionChannel.TRANSFER);
                outflow.setCurrency("EUR");
                outflow.setInflow(false);
                outflow.setRecipientId("primalac_" + i);
                outflow.setRecipientCountry(i % 3 == 0 ? "AF" : "RS");
                outflow.setRecipientIsForeignAccount(i % 2 == 0);
                outflow.setRecipientIsNewlyOpenedAccount(i % 3 == 0);
                session.insert(outflow);
            }
            session.fireAllRules();

            clock.advanceTime(10, TimeUnit.MINUTES);
            long tFinal = t0 + (12 * 20L * 60_000L) + (10L * 60_000L);
            result.addStep(formatTime(tFinal), "KORAK 3",
                    "Finalni transfer - okida accumulate pravila za muling pattern");

            Transaction lastOut = new Transaction("tx_mule_final",
                    stefan.getClientId(), 180.0, new Date(tFinal),
                    TransactionChannel.TRANSFER);
            lastOut.setCurrency("EUR");
            lastOut.setInflow(false);
            lastOut.setRecipientId("primalac_final");
            lastOut.setRecipientCountry("AF");
            lastOut.setRecipientIsForeignAccount(true);
            lastOut.setRecipientIsNewlyOpenedAccount(true);
            session.insert(lastOut);
            session.fireAllRules();

            return finalizeResult(result, session, stefan.getClientId(), "tx_mule_final");
        } finally {
            session.dispose();
        }
    }

    // =====================================================================
    // SCENARIO 3: APP Scam
    //
    // Aktivira pravila:
    //   Profil: Osetljiv segment (penzioner)
    //   L1: Prvi transfer ka novom primaocu
    //   L1: Abnormalno visok iznos
    //   L2: Obrazac pod stresom (2+ stres aktivnosti u 60m + L1 flagovi)
    //   L3: Osetljiv segment multiplikator (x1.3)
    //   L4: Visok rizik -> BLOCK_STEPUP (ne FREEZE jer je transakcija autorizovana)
    // =====================================================================
    public ScenarioResult runAppScamScenario() {
        ScenarioResult result = new ScenarioResult(
                "3. APP Scam - Autorizovana prevara",
                "Penzionerka pod uticajem laznog inspektora. Pre transfera "
                        + "pretrazuje limite, cita uputstvo za hitne prenose, pokusava transfer "
                        + "neuspesno. Konacno izvrsava transfer 6000 EUR. Demonstrira "
                        + "L3 multiplikator za osetljiv segment i suptilnu APP scam detekciju."
        );

        KieSession session = kieContainer.newKieSession("fraudPseudoSession");
        SessionPseudoClock clock = session.getSessionClock();

        try {
            long t0 = System.currentTimeMillis();
            clock.advanceTime(t0, TimeUnit.MILLISECONDS);

            Client olga = new Client("OLGA_003", "Olga P.", 72,
                    ClientSegment.PENZIONER, LocalDate.now().minusYears(3),
                    "RS", 800.0, 200.0);
            olga.getKnownDevices().add("Olgin-Tablet");
            olga.getKnownRecipients().add("Cerka-Marija");
            olga.getKnownRecipients().add("Komunalije");

            session.insert(olga);
            session.insert(defaultConfig());
            session.fireAllRules();

            result.addStep(formatTime(t0), "KORAK 1",
                    "Olga pod telefonom pretrazuje limite naloga");

            UserBehavior search = new UserBehavior(olga.getClientId(),
                    new Date(t0), UserBehavior.BehaviorType.LIMIT_SEARCH);
            session.insert(search);

            clock.advanceTime(5, TimeUnit.MINUTES);
            long t1 = t0 + 5L * 60_000L;
            result.addStep(formatTime(t1), "KORAK 2",
                    "Cita uputstvo za hitne prenose");

            UserBehavior helpRead = new UserBehavior(olga.getClientId(),
                    new Date(t1), UserBehavior.BehaviorType.URGENT_TRANSFER_HELP);
            session.insert(helpRead);

            clock.advanceTime(8, TimeUnit.MINUTES);
            long t2 = t0 + 13L * 60_000L;
            result.addStep(formatTime(t2), "KORAK 3",
                    "Neuspeli pokusaj transfera");

            UserBehavior failedAttempt = new UserBehavior(olga.getClientId(),
                    new Date(t2), UserBehavior.BehaviorType.FAILED_TRANSFER);
            session.insert(failedAttempt);
            session.fireAllRules();

            clock.advanceTime(10, TimeUnit.MINUTES);
            long tTransfer = t0 + 23L * 60_000L;
            result.addStep(formatTime(tTransfer), "KORAK 4",
                    "Olga izvrsava transfer 6000 EUR ka nepoznatom primaocu (30x prosek!)");

            Transaction transfer = new Transaction("tx_olga",
                    olga.getClientId(), 6000.0, new Date(tTransfer),
                    TransactionChannel.TRANSFER);
            transfer.setCurrency("EUR");
            transfer.setRecipientId("Laz-Inspektor-Racun");
            transfer.setRecipientCountry("RS");
            transfer.setDeviceId("Olgin-Tablet");
            transfer.setInflow(false);
            session.insert(transfer);
            session.fireAllRules();

            return finalizeResult(result, session, olga.getClientId(), "tx_olga");
        } finally {
            session.dispose();
        }
    }

    // =====================================================================
    // SCENARIO 4: Card Testing - Velocity Attack
    //
    // Aktivira pravila:
    //   CEP: Velocity attack (5+ tx u 60s)
    //   L1: Nepoznat uredjaj
    //   L1: Rizicna lokacija (AF)
    //   L3: VIP umanjenje (-20% za male iznose)
    //   L4: BLOCK_STEPUP
    // =====================================================================
    public ScenarioResult runCardTestingScenario() {
        ScenarioResult result = new ScenarioResult(
                "4. Card Testing - Velocity Attack",
                "Napadac testira ukradenu karticu - 7 malih transakcija u 50s. "
                        + "Demonstrira CEP velocity pravilo, VIP umanjenje skora i kako "
                        + "sistem i pored VIP statusa detektuje pattern."
        );

        KieSession session = kieContainer.newKieSession("fraudPseudoSession");
        SessionPseudoClock clock = session.getSessionClock();

        try {
            long t0 = System.currentTimeMillis();
            clock.advanceTime(t0, TimeUnit.MILLISECONDS);

            Client aleksandar = new Client("ALEKS_004", "Aleksandar S.", 38,
                    ClientSegment.VIP, LocalDate.now().minusYears(3),
                    "RS", 8000.0, 500.0);
            aleksandar.getKnownDevices().add("Aleks-iPhone");

            session.insert(aleksandar);
            session.insert(defaultConfig());
            session.fireAllRules();

            result.addStep(formatTime(t0), "KORAK 1",
                    "Napadac testira ukradenu karticu - serija od 7 transakcija");

            String[] merchants = {"merchant_a", "merchant_b", "merchant_c",
                    "merchant_d", "merchant_e", "merchant_f", "merchant_g"};

            for (int i = 0; i < 7; i++) {
                long ti = t0 + (i * 7000L);
                Transaction tx = new Transaction("tx_card_" + (i + 1),
                        aleksandar.getClientId(), 1.0 + (i * 0.50),
                        new Date(ti), TransactionChannel.ONLINE);
                tx.setCurrency("EUR");
                tx.setMerchantId(merchants[i]);
                tx.setMccCode(5732);
                tx.setDeviceId("Nepoznat-Uredjaj");
                tx.setCountry("AF");
                tx.setLatitude(34.5553);
                tx.setLongitude(69.2075);
                tx.setInflow(false);
                session.insert(tx);

                result.addStep(formatTime(ti), "TX " + (i + 1),
                        String.format("%.2f EUR na %s", tx.getAmount(), merchants[i]));

                if (i < 6) clock.advanceTime(7, TimeUnit.SECONDS);
            }
            session.fireAllRules();

            return finalizeResult(result, session, aleksandar.getClientId(), "tx_card_7");
        } finally {
            session.dispose();
        }
    }

    // =====================================================================
    // SCENARIO 5: Suspicious Profile
    //
    // Aktivira pravila:
    //   Profil: Novi racun sa visokim obrtom (15 dana, 8000 EUR/mesec)
    //   Profil: Klijent iz sive jurisdikcije (AF)
    //   Profil: Prethodna potvrdjena prevara
    //   Profil: KYC neuspeh
    //   Profil: Osetljiv segment (mladi, 22 god)
    //   L1: Nepoznat uredjaj
    // =====================================================================
    public ScenarioResult runSuspiciousProfileScenario() {
        ScenarioResult result = new ScenarioResult(
                "5. Suspicious Profile - rizican profil",
                "Klijent ima 5 indikatora rizika u profilu: mlad, novootvoren "
                        + "racun, visok obrt, prebivaliste u sivoj zemlji, prethodna prevara "
                        + "i neuspeli KYC. Demonstrira sva 5 pravila profila."
        );

        KieSession session = kieContainer.newKieSession("fraudPseudoSession");
        SessionPseudoClock clock = session.getSessionClock();

        try {
            long t0 = System.currentTimeMillis();
            clock.advanceTime(t0, TimeUnit.MILLISECONDS);

            Client risky = new Client("RISKY_005", "Petar K.", 22,
                    ClientSegment.MLADI, LocalDate.now().minusDays(15),
                    "AF", 8000.0, 300.0);
            risky.setHasPreviousFraudCase(true);
            risky.setKycFailed(true);

            session.insert(risky);
            session.insert(defaultConfig());
            session.fireAllRules();

            result.addStep(formatTime(t0), "KORAK 1",
                    "Klijent registrovan: 22 god, racun 15 dana, obrt 8000 EUR, AF");
            result.addStep(formatTime(t0), "KORAK 2",
                    "Klijent ima istoriju prevare i neuspeli KYC");

            clock.advanceTime(1, TimeUnit.MINUTES);
            long t1 = t0 + 60_000L;
            result.addStep(formatTime(t1), "KORAK 3",
                    "Klijent radi transakciju 500 EUR - okida sva pravila profila");

            Transaction tx = new Transaction("tx_risky", risky.getClientId(),
                    500.0, new Date(t1), TransactionChannel.POS);
            tx.setCurrency("EUR");
            tx.setCountry("RS");
            tx.setDeviceId("Nepoznat-Uredjaj");
            tx.setInflow(false);
            session.insert(tx);
            session.fireAllRules();

            return finalizeResult(result, session, risky.getClientId(), "tx_risky");
        } finally {
            session.dispose();
        }
    }

    // =====================================================================
    // SCENARIO 6: Night Gambling
    //
    // Aktivira pravila:
    //   L1: Neuobicajeno vreme (03:30h)
    //   L1: Rizican merchant (MCC 7995 - gambling)
    //   L1: Nepoznat uredjaj
    // =====================================================================
    public ScenarioResult runNightGamblingScenario() {
        ScenarioResult result = new ScenarioResult(
                "6. Night Gambling - nocna transakcija ka kockarskom sajtu",
                "Transakcija u 03:30 ujutru ka MCC 7995 (gambling) sa nepoznatog "
                        + "uredjaja. Demonstrira L1 pravila: NEUOBICAJENO_VREME (02-05h) "
                        + "i RIZICAN_MERCHANT (high-risk MCC kodovi)."
        );

        KieSession session = kieContainer.newKieSession("fraudPseudoSession");
        SessionPseudoClock clock = session.getSessionClock();

        try {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 3);
            cal.set(Calendar.MINUTE, 30);
            cal.set(Calendar.SECOND, 0);
            long t0 = cal.getTimeInMillis();
            clock.advanceTime(t0, TimeUnit.MILLISECONDS);

            Client darko = new Client("DARKO_006", "Darko M.", 35,
                    ClientSegment.REDOVNI, LocalDate.now().minusYears(2),
                    "RS", 1500.0, 200.0);
            darko.getKnownDevices().add("Darko-Telefon");

            session.insert(darko);
            session.insert(defaultConfig());
            session.fireAllRules();

            result.addStep(formatTime(t0), "KORAK 1",
                    "03:30h - transakcija ka kockarskom sajtu MCC 7995");

            Transaction tx = new Transaction("tx_gambling", darko.getClientId(),
                    400.0, new Date(t0), TransactionChannel.ONLINE);
            tx.setCurrency("EUR");
            tx.setMerchantId("BetSite365");
            tx.setMccCode(7995);
            tx.setDeviceId("Nepoznat-Uredjaj");
            tx.setCountry("RS");
            tx.setInflow(false);
            session.insert(tx);
            session.fireAllRules();

            return finalizeResult(result, session, darko.getClientId(), "tx_gambling");
        } finally {
            session.dispose();
        }
    }

    // =====================================================================
    // SCENARIO 7: Impossible Travel
    //
    // Aktivira pravila:
    //   CEP: Impossible travel (login) - Beograd -> Tokio za 30 min
    //   CEP: Impossible travel (transakcije)
    //   L1: Nepoznat uredjaj
    // =====================================================================
    public ScenarioResult runImpossibleTravelScenario() {
        ScenarioResult result = new ScenarioResult(
                "7. Impossible Travel - geografski nemoguce putovanje",
                "Login iz Beograda u 14:00, login iz Tokija u 14:30. Razdaljina "
                        + "9500km za 30min zahteva 19000 km/h - nemoguce. Demonstrira CEP "
                        + "geografsko-vremensku detekciju koja kombinuje GeoUtil sa Drools "
                        + "vremenskim operatorima."
        );

        KieSession session = kieContainer.newKieSession("fraudPseudoSession");
        SessionPseudoClock clock = session.getSessionClock();

        try {
            long t0 = System.currentTimeMillis();
            clock.advanceTime(t0, TimeUnit.MILLISECONDS);

            Client jovana = new Client("JOVANA_007", "Jovana T.", 32,
                    ClientSegment.REDOVNI, LocalDate.now().minusYears(4),
                    "RS", 3000.0, 400.0);
            jovana.getKnownDevices().add("Jovanin-Telefon");

            session.insert(jovana);
            session.insert(defaultConfig());
            session.fireAllRules();

            result.addStep(formatTime(t0), "KORAK 1",
                    "14:00 - login iz Beograda");

            LoginEvent loginBg = new LoginEvent("login_bg",
                    jovana.getClientId(), new Date(t0), true, "Jovanin-Telefon");
            loginBg.setCountry("RS");
            loginBg.setLatitude(44.7866);
            loginBg.setLongitude(20.4489);
            loginBg.setKnownDevice(true);
            session.insert(loginBg);
            session.fireAllRules();

            clock.advanceTime(30, TimeUnit.MINUTES);
            long t1 = t0 + 30L * 60_000L;
            result.addStep(formatTime(t1), "KORAK 2",
                    "14:30 - login iz Tokija (9500km u 30min nemoguce!)");

            LoginEvent loginTokyo = new LoginEvent("login_tokyo",
                    jovana.getClientId(), new Date(t1), true, "Napadac-Uredjaj");
            loginTokyo.setCountry("JP");
            loginTokyo.setLatitude(35.6762);
            loginTokyo.setLongitude(139.6503);
            loginTokyo.setKnownDevice(false);
            session.insert(loginTokyo);
            session.fireAllRules();

            clock.advanceTime(5, TimeUnit.MINUTES);
            long t2 = t0 + 35L * 60_000L;
            result.addStep(formatTime(t2), "KORAK 3",
                    "14:35 - transakcija 800 EUR iz Tokija");

            Transaction txBg = new Transaction("tx_bg_ref",
                    jovana.getClientId(), 100.0, new Date(t0), TransactionChannel.POS);
            txBg.setCountry("RS");
            txBg.setLatitude(44.7866);
            txBg.setLongitude(20.4489);
            txBg.setDeviceId("Jovanin-Telefon");
            txBg.setInflow(false);
            session.insert(txBg);

            Transaction txTokyo = new Transaction("tx_tokyo",
                    jovana.getClientId(), 800.0, new Date(t2), TransactionChannel.POS);
            txTokyo.setCountry("JP");
            txTokyo.setLatitude(35.6762);
            txTokyo.setLongitude(139.6503);
            txTokyo.setDeviceId("Napadac-Uredjaj");
            txTokyo.setInflow(false);
            session.insert(txTokyo);
            session.fireAllRules();

            return finalizeResult(result, session, jovana.getClientId(), "tx_tokyo");
        } finally {
            session.dispose();
        }
    }

    // =====================================================================
    // SCENARIO 8: Structuring (Smurfing)
    //
    // Aktivira pravila:
    //   CEP: Structuring (3+ tx u 48h, iznos u [85%, 100%) AML praga)
    //   L4: AML prijava
    // =====================================================================
    public ScenarioResult runStructuringScenario() {
        ScenarioResult result = new ScenarioResult(
                "8. Structuring (Smurfing) - razbijanje iznosa ispod AML praga",
                "Klijent izvrsava 4 transakcije po ~14000 EUR u 48h. Svaka je "
                        + "tacno ispod AML praga prijave (15000 EUR). Demonstrira CEP "
                        + "structuring detekciju i automatsku AML prijavu."
        );

        KieSession session = kieContainer.newKieSession("fraudPseudoSession");
        SessionPseudoClock clock = session.getSessionClock();

        try {
            long t0 = System.currentTimeMillis();
            clock.advanceTime(t0, TimeUnit.MILLISECONDS);

            Client milan = new Client("MILAN_008", "Milan B.", 50,
                    ClientSegment.REDOVNI, LocalDate.now().minusYears(8),
                    "RS", 10000.0, 1500.0);
            milan.getKnownDevices().add("Milan-Telefon");

            session.insert(milan);
            session.insert(defaultConfig());
            session.fireAllRules();

            double[] amounts = {14000.0, 14500.0, 13900.0, 14200.0};
            for (int i = 0; i < amounts.length; i++) {
                long ti = t0 + (i * 10L * 3600_000L);
                if (i > 0) clock.advanceTime(10, TimeUnit.HOURS);

                Transaction tx = new Transaction("tx_struct_" + (i + 1),
                        milan.getClientId(), amounts[i],
                        new Date(ti), TransactionChannel.TRANSFER);
                tx.setCurrency("EUR");
                tx.setRecipientId("primalac_" + (i + 1));
                tx.setDeviceId("Milan-Telefon");
                tx.setCountry("RS");
                tx.setInflow(false);
                session.insert(tx);

                result.addStep(formatTime(ti), "TX " + (i + 1),
                        String.format("%.0f EUR ispod AML praga", amounts[i]));
            }
            session.fireAllRules();

            return finalizeResult(result, session, milan.getClientId(), "tx_struct_4");
        } finally {
            session.dispose();
        }
    }

    // =====================================================================
    // SCENARIO 9: Happy Path
    //
    // Aktivira pravila: nijedno!
    // Demonstrira da sistem ne pravi lazne alarme za normalne transakcije.
    // Skor ostaje 0 (ili ne postoji RiskScore uopste).
    // =====================================================================
    public ScenarioResult runHappyPathScenario() {
        ScenarioResult result = new ScenarioResult(
                "9. Happy Path - normalna transakcija prolazi",
                "Klijent salje 150 EUR poznatom primaocu sa poznatog uredjaja "
                        + "u Beogradu. Demonstrira da sistem ne pravi lazne alarme i da "
                        + "se ne generise nijedan flag za legitimne transakcije."
        );

        KieSession session = kieContainer.newKieSession("fraudPseudoSession");
        SessionPseudoClock clock = session.getSessionClock();

        try {
            long t0 = System.currentTimeMillis();
            clock.advanceTime(t0, TimeUnit.MILLISECONDS);

            Client ana = new Client("ANA_009", "Ana S.", 40,
                    ClientSegment.REDOVNI, LocalDate.now().minusYears(5),
                    "RS", 2000.0, 300.0);
            ana.getKnownDevices().add("Ana-Telefon");
            ana.getKnownRecipients().add("Mama");

            session.insert(ana);
            session.insert(defaultConfig());
            session.fireAllRules();

            result.addStep(formatTime(t0), "KORAK 1",
                    "Ana salje 150 EUR mami sa svog telefona - sve normalno");

            Transaction tx = new Transaction("tx_normal", ana.getClientId(),
                    150.0, new Date(t0), TransactionChannel.TRANSFER);
            tx.setCurrency("EUR");
            tx.setRecipientId("Mama");
            tx.setRecipientCountry("RS");
            tx.setDeviceId("Ana-Telefon");
            tx.setCountry("RS");
            tx.setInflow(false);
            session.insert(tx);
            session.fireAllRules();

            return finalizeResult(result, session, ana.getClientId(), "tx_normal");
        } finally {
            session.dispose();
        }
    }

    // =====================================================================
    // SCENARIO 10: Cumulative Escalation
    //
    // Aktivira pravila:
    //   L4: Watchlist boost (MEDIUM -> HIGH za klijenta na watchlist-i)
    //   L4: Kumulativna eskalacija (3+ blokade u 7 dana)
    //   modify(Client) u L4 -> reaktivira pravila
    // =====================================================================
    public ScenarioResult runCumulativeEscalationScenario() {
        ScenarioResult result = new ScenarioResult(
                "10. Cumulative Escalation - watchlist + kumulativna eskalacija",
                "Klijent vec ima 3 blokade u 7 dana i nalazi se na watchlist-i. "
                        + "Pri novoj umereno sumnjivoj transakciji, sistem automatski "
                        + "eskalira preko Watchlist boost pravila i Kumulativne eskalacije. "
                        + "Demonstrira modify() i feedback petlju u Drools-u."
        );

        KieSession session = kieContainer.newKieSession("fraudPseudoSession");
        SessionPseudoClock clock = session.getSessionClock();

        try {
            long t0 = System.currentTimeMillis();
            clock.advanceTime(t0, TimeUnit.MILLISECONDS);

            Client nemanja = new Client("NEMANJA_010", "Nemanja V.", 45,
                    ClientSegment.REDOVNI, LocalDate.now().minusYears(6),
                    "RS", 2000.0, 250.0);
            nemanja.getKnownDevices().add("Nemanja-Telefon");
            nemanja.setOnWatchlist(true);
            nemanja.setBlockedTransactionsLast7Days(3);

            session.insert(nemanja);
            session.insert(defaultConfig());

            result.addStep(formatTime(t0), "KORAK 1",
                    "Nemanja vec na watchlist-i sa 3 blokade u 7 dana");

            session.fireAllRules();

            clock.advanceTime(1, TimeUnit.MINUTES);
            long t1 = t0 + 60_000L;
            result.addStep(formatTime(t1), "KORAK 2",
                    "Nova transakcija 600 EUR sa nepoznatog uredjaja ka nepoznatom primaocu");

            Transaction tx = new Transaction("tx_cumul", nemanja.getClientId(),
                    600.0, new Date(t1), TransactionChannel.TRANSFER);
            tx.setCurrency("EUR");
            tx.setRecipientId("Novi-Primalac");
            tx.setRecipientCountry("RS");
            tx.setDeviceId("Nepoznat-Uredjaj");
            tx.setCountry("RS");
            tx.setInflow(false);
            session.insert(tx);
            session.fireAllRules();

            return finalizeResult(result, session, nemanja.getClientId(), "tx_cumul");
        } finally {
            session.dispose();
        }
    }

    // =====================================================================
    // Helper metode
    // =====================================================================

    private ConfigList defaultConfig() {
        ConfigList config = new ConfigList();
        config.getGrayListedCountries().add("AF");
        config.getGrayListedCountries().add("SY");
        config.getGrayListedCountries().add("IR");
        config.getGrayListedCountries().add("JP");
        config.setAmlReportingThreshold(15000.0);
        return config;
    }

    private String formatTime(long ms) {
        return TIME_FMT.format(new Date(ms));
    }

    private ScenarioResult finalizeResult(ScenarioResult result,
                                          KieSession session,
                                          String clientId,
                                          String triggeringTxId) {
        List<Flag> flags = new ArrayList<>();
        for (Object o : session.getObjects()) {
            if (o instanceof Flag) {
                Flag f = (Flag) o;
                if (clientId.equals(f.getClientId())) flags.add(f);
            }
        }
        result.setFlags(flags);

        RiskScore latest = null;
        for (Object o : session.getObjects()) {
            if (o instanceof RiskScore) {
                RiskScore rs = (RiskScore) o;
                if (clientId.equals(rs.getClientId())) {
                    if (latest == null
                            || rs.getTimestamp().after(latest.getTimestamp())) {
                        latest = rs;
                    }
                }
            }
        }
        result.setFinalScore(latest);

        Alert highest = null;
        for (Object o : session.getObjects()) {
            if (o instanceof Alert) {
                Alert a = (Alert) o;
                if (triggeringTxId.equals(a.getTransactionId())) {
                    if (highest == null || a.getScore() > highest.getScore()) {
                        highest = a;
                    }
                }
            }
        }
        result.setFinalAlert(highest);

        if (highest != null && latest != null) {
            result.setSummary(String.format("[%s] %s | Skor: %.1f (%s) | %d flagova",
                    highest.getAction(), highest.getMessage(),
                    latest.getScore(), latest.getLevel(), flags.size()));
        } else if (latest != null) {
            result.setSummary(String.format("Skor: %.1f (%s) | %d flagova",
                    latest.getScore(), latest.getLevel(), flags.size()));
        } else {
            result.setSummary("Bez akcije - " + flags.size() + " flagova (normalna transakcija)");
        }

        return result;
    }
}