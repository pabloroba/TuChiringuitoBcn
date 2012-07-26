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
 *  Author : Pablo Ródenas Barquero <prodenas@tuchiringuitobcn.com>
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
 *  		 Raúl Román López <rroman@gsyc.es>
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
                "Bambú Beach Bar",
                "http://tuchiringuitobcn.com/bambu_beach.html",
                "El chiringuito Bambú Beach Bar, del grupo Pantea Group, "
                        +
                        "se encuentra ubicado en la última playa de Barcelona, "
                        +
                        "la playa de Llevant . Se caracteriza por una decoración "
                        +
                        "sencilla pero al mismo tiempo con toque exótico, "
                        +
                        "un ambiente familiar, tranquilo y relajante acompañado "
                        +
                        "por una buena selección de música que va variando según "
                        +
                        "el momento del día pero preferentemente chill-out y "
                        +
                        "chill-house.\n\n" +
                        "Cuenta con un equipo de gente joven que se "
                        +
                        "dedica desde hace muchos años al sector gastronómico y "
                        +
                        "dispuesta a dar lo mejor de sí para que disfrutes al máximo "
                        +
                        "de tus días de verano.\n\n"
                        +
                        "Enfocado a un target de clientes adultos y aficionados a "
                        +
                        "su trato amable y su gastronomía de calidad, Bambú Beach "
                        +
                        "Bar es el chiringuito ideal para disfrutar de deliciosa "
                        +
                        "comida, de un apetitoso aperitivo al atardecer y terminar "
                        +
                        "la noche con una velada acompañada de buena música, " +
                        "antorchas, hamacas y las luz de la luna.", // 0

                "Nueva Ola",
                "http://tuchiringuitobcn.com/nueva_ola.html",
                "Nueva Ola Beach Bar es paraíso isleño situado en la última "
                        +
                        "de las playas de Barcelona, la Playa Llevant, justo antes de "
                        +
                        "llegar al Forum. Se caracteriza por un ambiente alegre, una "
                        +
                        "decoración estilosa y bien cuidada y un público joven y "
                        +
                        "dinámico.  Dispone de música especialmente seleccionada según "
                        +
                        "la hora del día y de estilo prevalentemente house, "
                        +
                        "electrónica y beat-house.\n\n"
                        +
                        "El chiringuito ideal para que pases un rato agradable "
                        +
                        "delante del mardurante el día, con tragos de frutas naturales "
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
                        "y salud el Chiringuito Natura cuenta con una decoración "
                        +
                        "sencilla y acogedora y deleita sus clientes en cada momento "
                        +
                        "del día con buena selección de música chillout y de ambiente.\n\n"
                        +
                        "El chiringuito ideal para disfrutar de una comida saludable "
                        +
                        "y ecológica, tomar un sabroso cóctel al atardecer y acabar "
                        +
                        "el día con una cena romántica con cava a la luz de la luna.\n\n"
                        +
                        "Desconecta, relájate, vive el bienestar físico y mental en la playa.",

                "Surya",
                "http://tuchiringuitobcn.com/surya_beach.html",
                "El chiringuito Surya de recién apertura, se encuentra ubicado "
                        +
                        "en la emblemática playa de la Nova Mar Bella. Se "
                        +
                        "caracteriza por una decoración sencilla pero al mismo tiempo "
                        +
                        "con toque exótico, un ambiente familiar y ameno acompañado "
                        +
                        "por una buena selección de música que mezcla sonidos y "
                        +
                        "estilos de diferentes culturas y países reflejando el perfil "
                        +
                        "internacional de su equipo. Rock años 80, afro beach, "
                        +
                        "hip hop, soul, y jazz deleitan el día según el evento y el "
                        +
                        "momento alejándose un poco del típico ritmo house.\n\n"
                        +
                        "Enfocado a un target de clientes tanto adultos como jóvenes, "
                        +
                        "Surya es el chiringuito ideal para disfrutar de deliciosa "
                        +
                        "comida con sabores exóticos, un refrescante cóctel al "
                        +
                        "atardecer y terminar la noche con una velada acompañada de "
                        +
                        "buena música y el horizonte del mar.",

                "Gaudir",
                "http://tuchiringuitobcn.com/gaudir.html",
                "Chiringuito Gaudir, un rincón de paz situado en la playa "
                        +
                        "de la Nova Mar Bella y de recién abertura, se caracteriza "
                        +
                        "por un ambiente relajado y tranquilo, una decoración "
                        +
                        "acogedora y un público prevalentemente adulto.\n\n" +
                        "Envuelto de un ritmo blues y jazz, Gaudir dispone de " +
                        "música especialmente seleccionada según la hora del " +
                        "día para que pases un rato " +
                        "agradable disfrutando del horizonte del mar.\n\n"
                        +
                        "El chiringuito ideal para relajarse durante el día, con "
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
                        "familiar y acogedor y decoración sencilla pero al mismo "
                        +
                        "tiempo con un toque exotico.  Por su ubicación,algo más "
                        +
                        "alejada de la zona turística, atrae sobre todo jóvenes y "
                        +
                        "deportistas del barrio.\n\n"
                        +
                        "Recomendable para comer unas sabrosas tapas, un suculento "
                        +
                        "plato y tomarte una refrescante caña después de haberte "
                        +
                        "tostado al sol o haber dado un agradable paseo.", // 5

                "Relevant",
                "http://tuchiringuitobcn.com/relevant.html",
                "Chiringuito Relevant, también conocido como el \"Chiringuito "
                        +
                        "R\" o \"Relevante\" se encuentra ubicado en la playa de la "
                        +
                        "Mar Bella. Con su pasión por la cultura, lo desconocido y lo "
                        +
                        "novedoso intenta cada día reflejar en la playa su filosofía "
                        +
                        "de promover el  arte joven en Barcelona. De hecho Relevant "
                        +
                        "nace de una revista con el mismo nombre, cuyas páginas están "
                        +
                        "destinadas a lo mejor de la música, la cultura y el arte "
                        +
                        "urbano, siempre a la vanguardia de los intereses artísticos "
                        +
                        "de la gente.\n\n"
                        +
                        "En el Relevant cada momento del día es acompañado por música "
                        +
                        "de calidad que mezcla sonidos nuevos conjazz, soul, newpunk, "
                        +
                        "blues, afro y house. Ideal para disfrutar de un paisaje "
                        +
                        "relajante en un entorno de respeto, dejarse llevar por la "
                        +
                        "música disfrutando de comida sabrosa y cocteles refrescantes "
                        +
                        "y admirar una buena exposición de arte.",

                "Chiringuito de la Mar Bella \"Chirigay\"",
                "http://tuchiringuitobcn.com/chirigay.html",
                "ElChiringuito de la Mar Bella  se encuentra ubicado justo al "
                        +
                        "lado de la zona nudista de la playa de la Mar Bella y es uno "
                        +
                        "de los chiringuitos más carismáticos de todo el litoral "
                        +
                        "barcelonés además de ser el único de ambiente abiertamente "
                        +
                        "gay.\n\n" +
                        "Conocido también como “Chiringay”, el local ha terminado "
                        +
                        "por convertirse con derecho propio en todo un referente, "
                        +
                        "no sólo de la escena gay local, sino también internacional. "
                        +
                        "Así pues, durante los meses del verano concentra una "
                        +
                        "infinidad de turistas de todo el mundo.\n\n"
                        +
                        "En el Chiringuito de la Mar Bella cada momento del día es "
                        +
                        "acompañado por música de calidad que empieza con un estilo "
                        +
                        "brasileño por la mañana y se convierte en house a partir de "
                        +
                        "la tarde.\n\n"
                        +
                        "Ideal para disfrutar de buen ambiente, de diversión, "
                        +
                        "espontáneo y sin complejos, asistir a eventos culturales y "
                        +
                        "espectáculos de día y de noche y disfrutar de comida y "
                        +
                        "cócteles de calidad. ",

                "Vai Moana",
                "http://tuchiringuitobcn.com/vai_moana.html",
                "El chiringuito Vai Moana, del grupo Pantea Group, se "
                        +
                        "encuentra ubicado en la playa de Bogatell y cuenta con "
                        +
                        "un equipo de gente joven dispuesta a dar lo mejor de sí "
                        +
                        "para que disfrutes al máximo de tus días de verano. El "
                        +
                        "nombre Vai Moana tiene su origen en la Isla de Pascua, en "
                        +
                        "cuya lengua el Rapa Nui significa Mar Azul.\n\n" +
                        "Enfocado a un "
                        +
                        "target de clientes adultos, con una decoración sencilla "
                        +
                        "pero al mismo tiempo con toque exótico se caracteriza por "
                        +
                        "un ambiente chill out, tranquilo y relajante acompañado "
                        +
                        "por música lounge.\n\n"
                        +
                        "El chiringuito ideal para disfrutar de deliciosa comida, "
                        +
                        "de un apetitoso aperitivo al atardecer y terminar la noche "
                        +
                        "con una velada acompañada de buena música mientras observas el "
                        +
                        "mar iluminado solamente con la luz de la luna y de las velas.",

                "El Chiringuito Group Barcelona Beach",
                "http://tuchiringuitobcn.com/bogatell.html",
                "El Chiringuito Barcelona Beach, del grupo Chiringuito Group,"
                        +
                        "está situado en una de las playas más carismáticas y más "
                        +
                        "apreciadas por los barceloneses, Bogatell, se caracteriza "
                        +
                        "por un ambiente fresco y moderno con un público con espíritu "
                        +
                        "joven y de tendencias.\n\n" +
                        "Cada momento del día es acompañado "
                        +
                        "de una  buena selección de música house que se va a animando "
                        +
                        "a partir de la tarde cuando el sol baja y corre la brisa marina.\n\n"
                        +
                        "El chiringuito ideal para disfrutar de una buena comida, "
                        +
                        "tomar un refrescante cóctel al atardecer y acabar el día con "
                        +
                        "una cena romántica con cava a la luz de la luna.\n\n"
                        +
                        "¡Vive, saborea y comparte tus días de verano con el equipo "
                        +
                        "de Chiringuito Group!",

                "Inercia",
                "http://tuchiringuitobcn.com/inercia.html",
                "El chiringuito Inercia, del mismo grupo del Chiringuito "
                        +
                        "Docker`s, con una decoración como si de un oasis se "
                        +
                        "tratara es un pequeño paraíso en la playa de Nova Icaria y "
                        +
                        "se caracteriza por un ambiente tranquilo y relajante "
                        +
                        "durante el día y más animado y fiestero por la noche.\n\n" +
                        "A partir de las 14h se puede disfrutar de una exquisita "
                        +
                        "selección de música chill-out y de ambiente que cuando se "
                        +
                        "acerca la hora del aperitivo se anima más y se transforma "
                        +
                        "en música house y de percusión hasta las 2 de la madrugada. "
                        +
                        "Inercia cuenta con un amplio abanico de piezas musicales "
                        +
                        "que va adaptando según las exigencias de sus clientes y "
                        +
                        "eventos especiales que organiza.\n\n"
                        +
                        "El chiringuito ideal para disfrutar de una inolvidable "
                        +
                        "velada acompañada de buena música, de la brisa, del mar y "
                        +
                        "de un buen cóctel en el mejor ambiente ameno y tranquilo. "
                        +
                        "¡Vive la magia del día y la noche con el equipo de Inercia!", // 10

                "El Bierzo",
                "http://tuchiringuitobcn.com/bierzo.html",
                "El Bierzo, ubicado en la playa de Nova Icaria, es famoso "
                        +
                        "por su música reggae, en especial Ragamuffin. La "
                        +
                        "combinación de ritmos jamaicanos y el entorno de la playa "
                        +
                        "inspiran relajación y alegría a la vez. Con decoración "
                        +
                        "sencilla, ambiente relajado y amigable este chiringuito "
                        +
                        "es el lugar ideal para compartir o llevar a alguien que le "
                        +
                        "guste la cocina tradicional y para probar uno de sus "
                        +
                        "famosos mojitos o caipiriñas al atardecer/noche.\n\n"
                        +
                        "Disfruta y saborea tus momentos de verano a ritmo de reggae!",

                "Dockers",
                "http://tuchiringuitobcn.com/dockers.html",
                "El chiringuito Docker`s, del mismo grupo del Chiringuito "
                        +
                        "Inercia, con una decoración como si de un oasis se tratara, "
                        +
                        "es un pequeño paraíso en la playa de Nova Icària y se "
                        +
                        "caracteriza por un ambiente tranquilo y relajante durante "
                        +
                        "el día y más animado y fiestero por la noche.\n\n" +
                        "A partir de las 14h se puede disfrutar de una " +
                        "exquisita  selección de música chill-out  y de " +
                        "ambiente que cuando se acerca la hora "
                        +
                        "del aperitivo  se anima más y se transforma en música house "
                        +
                        "y de percusión hasta las 2 de la madrugada.\n\n" +
                        "El Docker`s cuenta con un amplio abanico de piezas " +
                        "musicales que va adaptando según las exigencias de " +
                        "sus clientes y eventos especiales que organiza.\n\n"
                        +
                        "El chiringuito ideal para disfrutar de una inolvidable "
                        +
                        "velada acompañadade buena música, de la brisa, del mar y "
                        +
                        "de un buen cóctel en el mejor ambiente ameno y tranquilo. "
                        +
                        "¡Vive la magia del día y la noche con el equipo de Docker`s!",

                "Blue Beach Bar",
                "http://tuchiringuitobcn.com/blue_beach.html",
                "Blue Beach Bar por su ubicación céntrica en el corazón de " +
                        "la playa de Sant Miquel atrae a muchos turistas que " +
                        "disfrutan gracias a su estilo y su buena atención al "
                        +
                        "cliente de momentos muy agradables, mar, sol, ricos " +
                        "cócteles , platos exquisitos y una terraza que te " +
                        "invita a quedarte todo el día.",

                "Ona Beach",
                "http://tuchiringuitobcn.com/ona_beach.html",
                "Ona Beach Bar, ubicado en la playa de San Miquel, "
                        +
                        "atrae tanto a turistas como a gente local gracias a su "
                        +
                        "situación centrica. Con una decoración sencilla y un "
                        +
                        "ambiente acogedor se caracteriza por ser un chiringuito "
                        +
                        "tradicional ideal para disfrutar del relax y un buen "
                        +
                        "cóctel después de haberte tostado al sol.",

                "Las Vegas",
                "http://tuchiringuitobcn.com/vegas.html",
                "Las Vegas, ubicado en la playa de Sant Miquel, es el "
                        +
                        "chiringuito tradicional de toda la vida con ambiente "
                        +
                        "familiar y acogedor y decoración sencilla pero al mismo "
                        +
                        "tiempo muy alegre y desenfadada. Por su céntrica ubicación "
                        +
                        "también atrae a muchos turistas que siempre quedan encantados "
                        +
                        "por el trato amable y servicios de calidad.\n\n"
                        +
                        "Recomendable para comer unas sabrosas tapas, un suculento plato"
                        +
                        " y tomarte una refrescante caña después de haberte tostado al sol.", // 15

                "Chiringuito del Sol",
                "http://tuchiringuitobcn.com/el_sol.html",
                "Chiringuito del Sol es el situado más a poniente de toda "
                        +
                        "la playa de Barcelona. Esta situación estratégica, hace "
                        +
                        "que quede justo al lado de los dos clubs náuticos de la "
                        +
                        "playa de Sant Sebastià y que atraiga muchos turistas además "
                        +
                        "de jóvenes del barrio. Cuenta ambiente tranquilo y acogedor "
                        +
                        "y un personal amistoso que hacen que los enamorados de la "
                        +
                        "playa se encontraran como en casa.\n\n"
                        +
                        "En el Chiringuito del Sol cada momento del día es acompañado "
                        +
                        "por una buena selección musical que empieza con ritmos jazz, "
                        +
                        "soul,, blues por la mañana para disfrutar de un ambiente "
                        +
                        "relajante durante el desayuno y se anima más a la lo largo "
                        +
                        "del día con ritmos house y reggae.\n\n"
                        +
                        "Ideal para disfrutar de deliciosos aperitivos, buena música "
                        +
                        "y maravillosos momentos frente al mar.",

                "Chiringuito del Mar",
                "http://tuchiringuitobcn.com/del_mar.html",
                "Chiringuito del Mar es el situado más a poniente de toda la "
                        +
                        "playa de Barcelona en la denominada Playa de San Sebastiá. "
                        +
                        "Su oferta es prácticamente idéntica a la de su chiringuito "
                        +
                        "gemelo, el Chiringuito del Mar. Cuenta con un equipo de "
                        +
                        "gente alegre y amable, un agradable ambiente, buena música "
                        +
                        "y deliciosa comida.\n\n" +
                        "En el Chiringuito del Mar cada momento "
                        +
                        "del día es acompañado por una buena selección musical que "
                        +
                        "empieza con ritmos jazz, soul, blues por la mañana para "
                        +
                        "disfrutar de un ambiente relajante durante el desayuno y "
                        +
                        "se anima más a la lo largo del día con ritmos house y reggae.\n\n"
                        +
                        "Ideal para disfrutar de con buena comida y cócteles "
                        +
                        "refrescantes que deleitarán tus días y noches de verano."
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
