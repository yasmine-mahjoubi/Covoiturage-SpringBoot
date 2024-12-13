package com.fst.projet_CarPooling_jee.Controller;

import com.fst.projet_CarPooling_jee.Entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

    //inject service class
    @Autowired
    private com.fst.projet_CarPooling_jee.Service.impl.UserService userService;

    //display list of users
    @GetMapping("/")
    public String viewHomePage(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", loggedInUser); // Ajoute l'utilisateur connecté au modèle
        return "index"; // Retourne la page d'accueil
    }
    @GetMapping("/showNewUserForm")
    public String showNewUserForm(Model model) {
        //create model attribute to bind from data
        User user = new User();
        //Thymeleaf template will access the empty user object binding form data
        model.addAttribute("user", user);
        return "new_user";
    }




    // Page de connexion (loginn)
    @GetMapping("/loginn")
    public String loginn() {
        return "loginn"; // Affiche la page de connexion
    }



    // Authentification de l'utilisateur
    @PostMapping("/loginn")
    public String authenticate(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        System.out.println("Email: " + email);  // Afficher l'email envoyé
        System.out.println("Password: " + password);  // Afficher le mot de passe envoyé

        User user = userService.authenticate(email, password); // Authentifier l'utilisateur

        if (user != null) {
            System.out.println("User authenticated: " + user.getFirstName());
            session.setAttribute("loggedInUser", user); // Enregistrer l'utilisateur dans la session
            session.setAttribute("loggedInUserId", user.getId());
            return "redirect:/"; // Rediriger vers la page d'accueil
        } else {
            System.out.println("Authentication failed.");
            model.addAttribute("error", "Email or password incorrect.");
            return "redirect:/loginn"; // Retourner à la page de connexion
        }
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            // Si aucun utilisateur n'est connecté, rediriger vers la page de connexion
            return "redirect:/loginn";
        }
        model.addAttribute("user", loggedInUser);
        return "profile";  // Page de profil de l'utilisateur
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Invalide la session et supprime tous les attributs
        return "redirect:/";  // Redirige vers la page d'accueil après la déconnexion
    }




    // Afficher le formulaire d'inscription
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";  // Retourne la vue register.html
    }

    // Traitement de l'enregistrement
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        System.out.println(user.getFirstName());
        System.out.println(user.getEmail());

        // Vérification si l'email existe déjà
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("error", "email already exists.");
            return "register";  // Retourne au formulaire d'inscription
        }

        // Optionnel : Hachage du mot de passe
    /*
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String hashedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashedPassword);
    */

        // Sauvegarde du nouvel utilisateur
        userService.saveUser(user);
        System.out.println("User saved successfully.");

        // Redirection vers la page de connexion ou accueil
        return "redirect:/loginn";  // Redirige vers la page de connexion après l'enregistrement
    }



    @GetMapping("/AllUsers")
    public String viewDrivers(Model model) {
        List<User> drivers = userService.getAllUsers();
        model.addAttribute("drivers", drivers);
        return "AllUsers";
    }

}