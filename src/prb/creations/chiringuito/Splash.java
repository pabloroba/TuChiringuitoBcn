/*
 * 
 *  Copyright (C) 2012 TuChiringuitoBcn.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/. 
 *
 *  Author : Pablo R�denas Barquero <prodenas@tuchiringuitobcn.com>
 *  
 *  Based on ARViewer of LibreGeoSocial.org:
 *
 *  Copyright (C) 2011 GSyC/LibreSoft, Universidad Rey Juan Carlos.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/. 
 *
 *  Author : Roberto Calvo Palomino <rocapal@gsyc.es>
 *  		 Juan Francisco Gato Luis <jfcogato@gsyc.es
 *  		 Ra�l Rom�n L�pez <rroman@gsyc.es>
 *
 */

package prb.creations.chiringuito;

import prb.creations.chiringuito.ARviewer.ARviewer;
import prb.creations.chiringuito.db.ChiringuitoProvider;
import prb.creations.chiringuito.db.ChiringuitosDB.Chiringuitos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.net.URL;

public class Splash extends Activity {
    private static final int ACTIVITY_RESULT = 1;

    private int sleepTime = 4000; // 4 seg
    protected Intent startIntent = null;

    private Handler altHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final ImageView iv = (ImageView) findViewById(R.id.iv_splash);
            Animation anim = AnimationUtils.loadAnimation(getBaseContext(),
                    R.anim.zoom_exit);
            anim.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    iv.setImageBitmap(null);
                    iv.invalidate();

                    if (!isFinishing() && (startIntent != null)) {
                        if (getIntent().getExtras() != null)
                            startIntent.putExtras(getIntent().getExtras());
                        startActivityForResult(startIntent, ACTIVITY_RESULT);
                    } else {
                        finish();
                    }
                }
            });
            ((RelativeLayout) findViewById(R.id.rl_splash))
                    .startAnimation(anim);

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        startIntent = new Intent(getBaseContext(), ARviewer.class);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

        Thread splashThread = new Thread() {

            @Override
            public void run() {

                try {
                    Thread.sleep(sleepTime);
                    if (altHandler != null)
                        altHandler.sendEmptyMessage(0);

                } catch (InterruptedException e) {
                    Log.e("Splash", "", e);
                }
            }
        };

        splashThread.start();

        generateChiringuitosDB();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case ACTIVITY_RESULT:

                if (resultCode != Activity.RESULT_CANCELED) {
                    setResult(RESULT_OK, data);
                }
                finish();
                break;

            default:
                break;
        }
    }

    private void generateChiringuitosDB() {
        resetContentResolver();
        String[] names = new String[] {
                "Bamb� Beach Bar",
                "http://tuchiringuitobcn.com/bambu_beach.html",
                "El chiringuito Bamb� Beach Bar, del grupo Pantea Group, "
                        +
                        "se encuentra ubicado en la �ltima playa de Barcelona, "
                        +
                        "la playa de Llevant . Se caracteriza por una decoraci�n "
                        +
                        "sencilla pero al mismo tiempo con toque ex�tico, "
                        +
                        "un ambiente familiar, tranquilo y relajante acompa�ado "
                        +
                        "por una buena selecci�n de m�sica que va variando seg�n "
                        +
                        "el momento del d�a pero preferentemente chill-out y "
                        +
                        "chill-house.\n\n" +
                        "Cuenta con un equipo de gente joven que se "
                        +
                        "dedica desde hace muchos a�os al sector gastron�mico y "
                        +
                        "dispuesta a dar lo mejor de s� para que disfrutes al m�ximo "
                        +
                        "de tus d�as de verano.\n\n"
                        +
                        "Enfocado a un target de clientes adultos y aficionados a "
                        +
                        "su trato amable y su gastronom�a de calidad, Bamb� Beach "
                        +
                        "Bar es el chiringuito ideal para disfrutar de deliciosa "
                        +
                        "comida, de un apetitoso aperitivo al atardecer y terminar "
                        +
                        "la noche con una velada acompa�ada de buena m�sica, " +
                        "antorchas, hamacas y las luz de la luna.", // 0

                "Nueva Ola",
                "http://tuchiringuitobcn.com/nueva_ola.html",
                "Nueva Ola Beach Bar es para�so isle�o situado en la �ltima "
                        +
                        "de las playas de Barcelona, la Playa Llevant, justo antes de "
                        +
                        "llegar al Forum. Se caracteriza por un ambiente alegre, una "
                        +
                        "decoraci�n estilosa y bien cuidada y un p�blico joven y "
                        +
                        "din�mico.  Dispone de m�sica especialmente seleccionada seg�n "
                        +
                        "la hora del d�a y de estilo prevalentemente house, "
                        +
                        "electr�nica y beat-house.\n\n"
                        +
                        "El chiringuito ideal para que pases un rato agradable "
                        +
                        "delante del mardurante el d�a, con tragos de frutas naturales "
                        +
                        "y un servicio de desayuno y comida de primer nivel.\n\n"
                        +
                        "Al atardecer y en la noche ideal para una cena a la luz de las "
                        +
                        "velas y de las estrellas.",

                "El Chiringuito Group, Natura",
                "http://tuchiringuitobcn.com/natura.html",
                "El Chiringuito Natura, del grupo Chiringuito Group, situado "
                        +
                        "en la playa de la Nova Mar Bella, se caracteriza por un "
                        +
                        "ambiente tranquilo y relajado ideal para desconectar del "
                        +
                        "bullicio de la ciudad. Inspirado a un concepto de bienestar "
                        +
                        "y salud el Chiringuito Natura cuenta con una decoraci�n "
                        +
                        "sencilla y acogedora y deleita sus clientes en cada momento "
                        +
                        "del d�a con buena selecci�n de m�sica chillout y de ambiente.\n\n"
                        +
                        "El chiringuito ideal para disfrutar de una comida saludable "
                        +
                        "y ecol�gica, tomar un sabroso c�ctel al atardecer y acabar "
                        +
                        "el d�a con una cena rom�ntica con cava a la luz de la luna.\n\n"
                        +
                        "Desconecta, rel�jate, vive el bienestar f�sico y mental en la playa.",

                "Surya",
                "http://tuchiringuitobcn.com/surya_beach.html",
                "El chiringuito Surya de reci�n apertura, se encuentra ubicado "
                        +
                        "en la emblem�tica playa de la Nova Mar Bella. Se "
                        +
                        "caracteriza por una decoraci�n sencilla pero al mismo tiempo "
                        +
                        "con toque ex�tico, un ambiente familiar y ameno acompa�ado "
                        +
                        "por una buena selecci�n de m�sica que mezcla sonidos y "
                        +
                        "estilos de diferentes culturas y pa�ses reflejando el perfil "
                        +
                        "internacional de su equipo. Rock a�os 80, afro beach, "
                        +
                        "hip hop, soul, y jazz deleitan el d�a seg�n el evento y el "
                        +
                        "momento alej�ndose un poco del t�pico ritmo house.\n\n"
                        +
                        "Enfocado a un target de clientes tanto adultos como j�venes, "
                        +
                        "Surya es el chiringuito ideal para disfrutar de deliciosa "
                        +
                        "comida con sabores ex�ticos, un refrescante c�ctel al "
                        +
                        "atardecer y terminar la noche con una velada acompa�ada de "
                        +
                        "buena m�sica y el horizonte del mar.",

                "Gaudir",
                "http://tuchiringuitobcn.com/gaudir.html",
                "Chiringuito Gaudir, un rinc�n de paz situado en la playa "
                        +
                        "de la Nova Mar Bella y de reci�n abertura, se caracteriza "
                        +
                        "por un ambiente relajado y tranquilo, una decoraci�n "
                        +
                        "acogedora y un p�blico prevalentemente adulto.\n\n" +
                        "Envuelto de un ritmo blues y jazz, Gaudir dispone de " +
                        "m�sica especialmente seleccionada seg�n la hora del " +
                        "d�a para que pases un rato " +
                        "agradable disfrutando del horizonte del mar.\n\n"
                        +
                        "El chiringuito ideal para relajarse durante el d�a, con "
                        +
                        "tragos de frutas naturales y un servicio de desayuno y "
                        +
                        "comida de primer nivel. Al atardecer y en la noche ideal "
                        +
                        "para una cena a la luz de las velas y de las estrellas.",

                "Sahara Beach Bar",
                "http://tuchiringuitobcn.com/sahara_beach.html",
                "Sahara Beach Bar, ubicado en la playa de la Nova Mar Bella, "
                        +
                        "es el chiringuito tradicional de toda la vida con ambiente "
                        +
                        "familiar y acogedor y decoraci�n sencilla pero al mismo "
                        +
                        "tiempo con un toque exotico.  Por su ubicaci�n,algo m�s "
                        +
                        "alejada de la zona tur�stica, atrae sobre todo j�venes y "
                        +
                        "deportistas del barrio.\n\n"
                        +
                        "Recomendable para comer unas sabrosas tapas, un suculento "
                        +
                        "plato y tomarte una refrescante ca�a despu�s de haberte "
                        +
                        "tostado al sol o haber dado un agradable paseo.", // 5

                "Relevant",
                "http://tuchiringuitobcn.com/relevant.html",
                "Chiringuito Relevant, tambi�n conocido como el \"Chiringuito "
                        +
                        "R\" o \"Relevante\" se encuentra ubicado en la playa de la "
                        +
                        "Mar Bella. Con su pasi�n por la cultura, lo desconocido y lo "
                        +
                        "novedoso intenta cada d�a reflejar en la playa su filosof�a "
                        +
                        "de promover el  arte joven en Barcelona. De hecho Relevant "
                        +
                        "nace de una revista con el mismo nombre, cuyas p�ginas est�n "
                        +
                        "destinadas a lo mejor de la m�sica, la cultura y el arte "
                        +
                        "urbano, siempre a la vanguardia de los intereses art�sticos "
                        +
                        "de la gente.\n\n"
                        +
                        "En el Relevant cada momento del d�a es acompa�ado por m�sica "
                        +
                        "de calidad que mezcla sonidos nuevos conjazz, soul, newpunk, "
                        +
                        "blues, afro y house. Ideal para disfrutar de un paisaje "
                        +
                        "relajante en un entorno de respeto, dejarse llevar por la "
                        +
                        "m�sica disfrutando de comida sabrosa y cocteles refrescantes "
                        +
                        "y admirar una buena exposici�n de arte.",

                "Chiringuito de la Mar Bella \"Chirigay\"",
                "http://tuchiringuitobcn.com/chirigay.html",
                "ElChiringuito de la Mar Bella  se encuentra ubicado justo al "
                        +
                        "lado de la zona nudista de la playa de la Mar Bella y es uno "
                        +
                        "de los chiringuitos m�s carism�ticos de todo el litoral "
                        +
                        "barcelon�s adem�s de ser el �nico de ambiente abiertamente "
                        +
                        "gay.\n\n" +
                        "Conocido tambi�n como �Chiringay�, el local ha terminado "
                        +
                        "por convertirse con derecho propio en todo un referente, "
                        +
                        "no s�lo de la escena gay local, sino tambi�n internacional. "
                        +
                        "As� pues, durante los meses del verano concentra una "
                        +
                        "infinidad de turistas de todo el mundo.\n\n"
                        +
                        "En el Chiringuito de la Mar Bella cada momento del d�a es "
                        +
                        "acompa�ado por m�sica de calidad que empieza con un estilo "
                        +
                        "brasile�o por la ma�ana y se convierte en house a partir de "
                        +
                        "la tarde.\n\n"
                        +
                        "Ideal para disfrutar de buen ambiente, de diversi�n, "
                        +
                        "espont�neo y sin complejos, asistir a eventos culturales y "
                        +
                        "espect�culos de d�a y de noche y disfrutar de comida y "
                        +
                        "c�cteles de calidad. ",

                "Vai Moana",
                "http://tuchiringuitobcn.com/vai_moana.html",
                "El chiringuito Vai Moana, del grupo Pantea Group, se "
                        +
                        "encuentra ubicado en la playa de Bogatell y cuenta con "
                        +
                        "un equipo de gente joven dispuesta a dar lo mejor de s� "
                        +
                        "para que disfrutes al m�ximo de tus d�as de verano. El "
                        +
                        "nombre Vai Moana tiene su origen en la Isla de Pascua, en "
                        +
                        "cuya lengua el Rapa Nui significa Mar Azul.\n\n" +
                        "Enfocado a un "
                        +
                        "target de clientes adultos, con una decoraci�n sencilla "
                        +
                        "pero al mismo tiempo con toque ex�tico se caracteriza por "
                        +
                        "un ambiente chill out, tranquilo y relajante acompa�ado "
                        +
                        "por m�sica lounge.\n\n"
                        +
                        "El chiringuito ideal para disfrutar de deliciosa comida, "
                        +
                        "de un apetitoso aperitivo al atardecer y terminar la noche "
                        +
                        "con una velada acompa�ada de buena m�sica mientras observas el "
                        +
                        "mar iluminado solamente con la luz de la luna y de las velas.",

                "El Chiringuito Group Barcelona Beach",
                "http://tuchiringuitobcn.com/bogatell.html",
                "El Chiringuito Barcelona Beach, del grupo Chiringuito Group,"
                        +
                        "est� situado en una de las playas m�s carism�ticas y m�s "
                        +
                        "apreciadas por los barceloneses, Bogatell, se caracteriza "
                        +
                        "por un ambiente fresco y moderno con un p�blico con esp�ritu "
                        +
                        "joven y de tendencias.\n\n" +
                        "Cada momento del d�a es acompa�ado "
                        +
                        "de una  buena selecci�n de m�sica house que se va a animando "
                        +
                        "a partir de la tarde cuando el sol baja y corre la brisa marina.\n\n"
                        +
                        "El chiringuito ideal para disfrutar de una buena comida, "
                        +
                        "tomar un refrescante c�ctel al atardecer y acabar el d�a con "
                        +
                        "una cena rom�ntica con cava a la luz de la luna.\n\n"
                        +
                        "�Vive, saborea y comparte tus d�as de verano con el equipo "
                        +
                        "de Chiringuito Group!",

                "Inercia",
                "http://tuchiringuitobcn.com/inercia.html",
                "El chiringuito Inercia, del mismo grupo del Chiringuito "
                        +
                        "Docker`s, con una decoraci�n como si de un oasis se "
                        +
                        "tratara es un peque�o para�so en la playa de Nova Icaria y "
                        +
                        "se caracteriza por un ambiente tranquilo y relajante "
                        +
                        "durante el d�a y m�s animado y fiestero por la noche.\n\n" +
                        "A partir de las 14h se puede disfrutar de una exquisita "
                        +
                        "selecci�n de m�sica chill-out y de ambiente que cuando se "
                        +
                        "acerca la hora del aperitivo se anima m�s y se transforma "
                        +
                        "en m�sica house y de percusi�n hasta las 2 de la madrugada. "
                        +
                        "Inercia cuenta con un amplio abanico de piezas musicales "
                        +
                        "que va adaptando seg�n las exigencias de sus clientes y "
                        +
                        "eventos especiales que organiza.\n\n"
                        +
                        "El chiringuito ideal para disfrutar de una inolvidable "
                        +
                        "velada acompa�ada de buena m�sica, de la brisa, del mar y "
                        +
                        "de un buen c�ctel en el mejor ambiente ameno y tranquilo. "
                        +
                        "�Vive la magia del d�a y la noche con el equipo de Inercia!", // 10

                "El Bierzo",
                "http://tuchiringuitobcn.com/bierzo.html",
                "El Bierzo, ubicado en la playa de Nova Icaria, es famoso "
                        +
                        "por su m�sica reggae, en especial Ragamuffin. La "
                        +
                        "combinaci�n de ritmos jamaicanos y el entorno de la playa "
                        +
                        "inspiran relajaci�n y alegr�a a la vez. Con decoraci�n "
                        +
                        "sencilla, ambiente relajado y amigable este chiringuito "
                        +
                        "es el lugar ideal para compartir o llevar a alguien que le "
                        +
                        "guste la cocina tradicional y para probar uno de sus "
                        +
                        "famosos mojitos o caipiri�as al atardecer/noche.\n\n"
                        +
                        "Disfruta y saborea tus momentos de verano a ritmo de reggae!",

                "Dockers",
                "http://tuchiringuitobcn.com/dockers.html",
                "El chiringuito Docker`s, del mismo grupo del Chiringuito "
                        +
                        "Inercia, con una decoraci�n como si de un oasis se tratara, "
                        +
                        "es un peque�o para�so en la playa de Nova Ic�ria y se "
                        +
                        "caracteriza por un ambiente tranquilo y relajante durante "
                        +
                        "el d�a y m�s animado y fiestero por la noche.\n\n" +
                        "A partir de las 14h se puede disfrutar de una " +
                        "exquisita  selecci�n de m�sica chill-out  y de " +
                        "ambiente que cuando se acerca la hora "
                        +
                        "del aperitivo  se anima m�s y se transforma en m�sica house "
                        +
                        "y de percusi�n hasta las 2 de la madrugada.\n\n" +
                        "El Docker`s cuenta con un amplio abanico de piezas " +
                        "musicales que va adaptando seg�n las exigencias de " +
                        "sus clientes y eventos especiales que organiza.\n\n"
                        +
                        "El chiringuito ideal para disfrutar de una inolvidable "
                        +
                        "velada acompa�adade buena m�sica, de la brisa, del mar y "
                        +
                        "de un buen c�ctel en el mejor ambiente ameno y tranquilo. "
                        +
                        "�Vive la magia del d�a y la noche con el equipo de Docker`s!",

                "Blue Beach Bar",
                "http://tuchiringuitobcn.com/blue_beach.html",
                "Blue Beach Bar por su ubicaci�n c�ntrica en el coraz�n de " +
                        "la playa de Sant Miquel atrae a muchos turistas que " +
                        "disfrutan gracias a su estilo y su buena atenci�n al "
                        +
                        "cliente de momentos muy agradables, mar, sol, ricos " +
                        "c�cteles , platos exquisitos y una terraza que te " +
                        "invita a quedarte todo el d�a.",

                "Ona Beach",
                "http://tuchiringuitobcn.com/ona_beach.html",
                "Ona Beach Bar, ubicado en la playa de San Miquel, "
                        +
                        "atrae tanto a turistas como a gente local gracias a su "
                        +
                        "situaci�n centrica. Con una decoraci�n sencilla y un "
                        +
                        "ambiente acogedor se caracteriza por ser un chiringuito "
                        +
                        "tradicional ideal para disfrutar del relax y un buen "
                        +
                        "c�ctel despu�s de haberte tostado al sol.",

                "Las Vegas",
                "http://tuchiringuitobcn.com/vegas.html",
                "Las Vegas, ubicado en la playa de Sant Miquel, es el "
                        +
                        "chiringuito tradicional de toda la vida con ambiente "
                        +
                        "familiar y acogedor y decoraci�n sencilla pero al mismo "
                        +
                        "tiempo muy alegre y desenfadada. Por su c�ntrica ubicaci�n "
                        +
                        "tambi�n atrae a muchos turistas que siempre quedan encantados "
                        +
                        "por el trato amable y servicios de calidad.\n\n"
                        +
                        "Recomendable para comer unas sabrosas tapas, un suculento plato"
                        +
                        " y tomarte una refrescante ca�a despu�s de haberte tostado al sol.", // 15

                "Chiringuito del Sol",
                "http://tuchiringuitobcn.com/el_sol.html",
                "Chiringuito del Sol es el situado m�s a poniente de toda "
                        +
                        "la playa de Barcelona. Esta situaci�n estrat�gica, hace "
                        +
                        "que quede justo al lado de los dos clubs n�uticos de la "
                        +
                        "playa de Sant Sebasti� y que atraiga muchos turistas adem�s "
                        +
                        "de j�venes del barrio. Cuenta ambiente tranquilo y acogedor "
                        +
                        "y un personal amistoso que hacen que los enamorados de la "
                        +
                        "playa se encontraran como en casa.\n\n"
                        +
                        "En el Chiringuito del Sol cada momento del d�a es acompa�ado "
                        +
                        "por una buena selecci�n musical que empieza con ritmos jazz, "
                        +
                        "soul,, blues por la ma�ana para disfrutar de un ambiente "
                        +
                        "relajante durante el desayuno y se anima m�s a la lo largo "
                        +
                        "del d�a con ritmos house y reggae.\n\n"
                        +
                        "Ideal para disfrutar de deliciosos aperitivos, buena m�sica "
                        +
                        "y maravillosos momentos frente al mar.",

                "Chiringuito del Mar",
                "http://tuchiringuitobcn.com/del_mar.html",
                "Chiringuito del Mar es el situado m�s a poniente de toda la "
                        +
                        "playa de Barcelona en la denominada Playa de San Sebasti�. "
                        +
                        "Su oferta es pr�cticamente id�ntica a la de su chiringuito "
                        +
                        "gemelo, el Chiringuito del Mar. Cuenta con un equipo de "
                        +
                        "gente alegre y amable, un agradable ambiente, buena m�sica "
                        +
                        "y deliciosa comida.\n\n" +
                        "En el Chiringuito del Mar cada momento "
                        +
                        "del d�a es acompa�ado por una buena selecci�n musical que "
                        +
                        "empieza con ritmos jazz, soul, blues por la ma�ana para "
                        +
                        "disfrutar de un ambiente relajante durante el desayuno y "
                        +
                        "se anima m�s a la lo largo del d�a con ritmos house y reggae.\n\n"
                        +
                        "Ideal para disfrutar de con buena comida y c�cteles "
                        +
                        "refrescantes que deleitar�n tus d�as y noches de verano."
        };
        float[] locations = new float[] {
                41.4051226879f, 2.21879764324f, // 0
                41.4040818236f, 2.2174634124f,
                41.4028276399f, 2.2158196399f,
                41.4017123442f, 2.21476888301f,
                41.400741804f, 2.21441717848f,
                41.3996602875f, 2.21291430381f, // 5
                41.3989950999f, 2.21227233409f,
                41.398355058f, 2.211685182f,
                41.3942596617f, 2.20664497789f,
                41.3934297698f, 2.20564769943f,
                41.3912307783f, 2.20357183807f, // 10
                41.3909193069f, 2.20233173597f,
                41.3902015648f, 2.20121820055f,
                41.37760776f, 2.19171547289f,
                41.3768300874f, 2.19116285422f,
                41.375870276f, 2.19058685005f, // 15
                41.3749301621f, 2.19019826517f,
                41.3735579612f, 2.18949183863f,
        };

        int n = (int) (names.length / 3);
        for (int i = 0; i < n; i++) {
            ContentValues values = new ContentValues();
            values.put(Chiringuitos.NAME, names[3 * i]);
            values.put(Chiringuitos.WEB_LINK, names[3 * i + 1]);
            values.put(Chiringuitos.INFO, names[3 * i + 2]);
            values.put(Chiringuitos.LATITUDE, locations[2 * i]);
            values.put(Chiringuitos.LONGITUDE, locations[2 * i + 1]);
            URL url = getClassLoader().getResource(
                    "res/drawable/chir" + i + ".png");
            if (url != null)
                values.put(Chiringuitos.PHOTO, url.toString());

            getContentResolver()
                    .insert(ChiringuitoProvider.CONTENT_URI, values);
        }
    }

    private void resetContentResolver() {
        getContentResolver()
                .delete(ChiringuitoProvider.CONTENT_URI, null, null);
    }

}
