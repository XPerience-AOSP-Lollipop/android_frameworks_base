/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.commands.vr;

import android.app.CompatibilityDisplayProperties;
import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;

import android.service.vr.IVrManager;
import com.android.internal.os.BaseCommand;

import java.io.PrintStream;

public final class Vr extends BaseCommand {

    /**
     * Command-line entry point.
     *
     * @param args The command-line arguments
     */
    public static void main(String[] args) {
      (new Vr()).run(args);
    }

    private static final String COMMAND_SET_PERSISTENT_VR_MODE_ENABLED =
        "set-persistent-vr-mode-enabled";
    private static final String COMMAND_SET_COMPATIBILITY_DISPLAY_PROPERTIES =
        "set-display-props";

    private IVrManager mVrService;

    @Override
    public void onShowUsage(PrintStream out) {
        out.println(
                "usage: vr [subcommand]\n" +
                "usage: vr set-persistent-vr-mode-enabled [true|false]\n" +
                "usage: vr set-display-props [width] [height] [dpi]\n"
                );
    }

    @Override
    public void onRun() throws Exception {
        mVrService = IVrManager.Stub.asInterface(ServiceManager.getService(Context.VR_SERVICE));
        if (mVrService == null) {
            showError("Error: Could not access the Vr Manager. Is the system running?");
            return;
        }

        String command = nextArgRequired();
        switch (command) {
            case COMMAND_SET_COMPATIBILITY_DISPLAY_PROPERTIES:
                runSetCompatibilityDisplayProperties();
                break;
            case COMMAND_SET_PERSISTENT_VR_MODE_ENABLED:
                runSetPersistentVrModeEnabled();
                break;
            default:
                throw new IllegalArgumentException ("unknown command '" + command + "'");
        }
    }

    private void runSetCompatibilityDisplayProperties() throws RemoteException {
        String widthStr = nextArgRequired();
        int width = Integer.parseInt(widthStr);

        String heightStr = nextArgRequired();
        int height = Integer.parseInt(heightStr);

        String dpiStr = nextArgRequired();
        int dpi = Integer.parseInt(dpiStr);

        CompatibilityDisplayProperties compatDisplayProperties =
                new CompatibilityDisplayProperties(width, height, dpi);

        try {
            mVrService.setCompatibilityDisplayProperties(compatDisplayProperties);
        } catch (RemoteException re) {
            System.err.println("Error: Can't set persistent mode " + re);
        }
    }

    private void runSetPersistentVrModeEnabled() throws RemoteException {
        String enableStr = nextArg();
        boolean enabled = Boolean.parseBoolean(enableStr);
        try {
            mVrService.setPersistentVrModeEnabled(enabled);
        } catch (RemoteException re) {
            System.err.println("Error: Can't set persistent mode " + re);
        }
    }
}
