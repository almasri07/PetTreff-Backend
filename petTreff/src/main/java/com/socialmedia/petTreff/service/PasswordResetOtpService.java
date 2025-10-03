package com.socialmedia.petTreff.service;


import com.socialmedia.petTreff.config.AppProperties;
import com.socialmedia.petTreff.entity.PasswordResetCode;
import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.repository.PasswordResetCodeRepository;
import com.socialmedia.petTreff.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;




import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.http.HttpStatus.*;


@Service
@RequiredArgsConstructor
public class PasswordResetOtpService {

    private final PasswordResetCodeRepository codeRepo;
    private final UserRepository userRepo; // deine bestehende UserRepo
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties; // enthält z.B. mail-from (optional)



    private String genCode() {
        int v = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return String.format("%06d", v);
    }

    @Transactional
    public void start(String email) {
        // NICHTS zurückgeben, um zu vermeiden, dass man erkennt, ob Mail existiert
        userRepo.findByEmail(email).ifPresent(user -> {
            PasswordResetCode prc = new PasswordResetCode();
            prc.setEmail(email);
            prc.setCode(genCode());
            prc.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            prc.setUsed(false);
            prc.setAttempts(0);
            codeRepo.save(prc);

            // E-Mail senden
            sendResetEmail(email, prc.getCode());
        });

    }

    private void sendResetEmail(String to, String code) {
        String from = appProperties.getMail().getFrom() != null ? appProperties.getMail().getFrom() : "no-reply@localhost";
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("Password Reset — Your Code");
        msg.setText("Your password reset code is: " + code + "\n\n"
                + "Valid for 15 minutes. If you did not request this, please ignore this email.");
        mailSender.send(msg);
    }


    @Transactional
    public void verifyAndReset(String email, String code, String newPassword) {
        PasswordResetCode prc = codeRepo.findTopByEmailAndUsedFalseOrderByIdDesc(email)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Invalid code"));

        if (prc.getExpiresAt().isBefore(LocalDateTime.now()) || prc.isUsed()) {
            throw new ResponseStatusException(BAD_REQUEST, "Code expired or already used");
        }

        if (!prc.getCode().equals(code)) {
            prc.setAttempts(prc.getAttempts() + 1);
            codeRepo.save(prc);
            if (prc.getAttempts() >= 5) {
                throw new ResponseStatusException(TOO_MANY_REQUESTS, " Too many failed attempts"); //Zu viele Fehlversuche
            }
            throw new ResponseStatusException(BAD_REQUEST, "Incorrect code");
        }

        // Code ist korrekt
        User user = (User) userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Email not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        prc.setUsed(true);
        userRepo.save(user);
        codeRepo.save(prc);
    }


    @Transactional(readOnly = true)
    public void checkCode(String email, String code) {
        PasswordResetCode prc = codeRepo.findTopByEmailAndUsedFalseOrderByIdDesc(email)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Code not found"));

        if (prc.getExpiresAt().isBefore(LocalDateTime.now()) || prc.isUsed()) {
            throw new ResponseStatusException(BAD_REQUEST, "Code expired or already used");
        }

        if (!prc.getCode().equals(code)) {
            // increment attempts (non-readonly -> do small update)
            prc.setAttempts(prc.getAttempts() + 1);
            codeRepo.save(prc);
            if (prc.getAttempts() >= 5) {
                throw new ResponseStatusException(TOO_MANY_REQUESTS, "Too many wrong attempts");
            }
            throw new ResponseStatusException(BAD_REQUEST, "Invalid code");
        }

        // if equals -> just return (204). Do NOT mark as used here.
    }

}
