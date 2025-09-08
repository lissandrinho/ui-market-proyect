package com.menu.uimarketsolo;

import com.menu.uimarketsolo.model.Usuario;

public class SessionManager {

    //Instancia única de SessionManager
    private static SessionManager instance;

    //Usuario que ha iniciado sesión
    private Usuario usuarioLogueado;

    //Constructor vacio para que nadie mas pueda crear instancias
    private SessionManager(){

    }

    //Devuelve la única instancia del SessionManager.
    public static SessionManager getInstance(){
        if(instance == null){
            instance = new SessionManager();
        }
        return instance;
    }

    //Guarda el usuario que ha iniciado sesión
    public void login(Usuario usuario){
        this.usuarioLogueado = usuario;
    }

    //Cierra la sesión del usuario actual
    public void logOut(){
        this.usuarioLogueado = null;
    }

    //Devuelve el usuario que ha iniciado sesión
    public Usuario getUsuarioLogueado() {
        return this.usuarioLogueado;
    }

}
