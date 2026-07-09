solution "steamworks4j"
	configurations { "ReleaseDLL" }
	platforms { "x64" }

	includedirs {
		"../src/main/native/include/jni",
		"../src/main/native/include/jni/win32",
		"../sdk/public/steam"
	}

	defines {
		"NDEBUG",
		"WINDOWS"
	}

    optimize "On"
    staticruntime "On"

	buildoptions {
		"/wd4800",
		"/wd4996"
	}

	project "steamworks4j"

		kind "SharedLib"
		language "C++"

		files {
			"../src/main/native/**.cpp"
		}

		includedirs {
			"../src/main/native",
			"../sdk/public"
		}

		libdirs {
			"../sdk/redistributable_bin/win64"
		}
		links {
			"steam_api64"
		}

	project "steamworks4j-server"

		kind "SharedLib"
		language "C++"

		files {
			"../server/src/main/native/**.cpp"
		}

		excludes {
			"../server/src/main/native/**EncryptedAppTicket*.cpp"
		}

		includedirs {
			"../server/src/main/native"
		}

		libdirs {
			"../sdk/redistributable_bin/win64"
		}
		links {
			"steam_api64"
		}

	project "steamworks4j-encryptedappticket"

		kind "SharedLib"
		language "C++"

		files {
			"../server/src/main/native/**EncryptedAppTicket*.cpp"
		}

		includedirs {
			"../server/src/main/native"
		}

		libdirs {
			"../sdk/public/steam/lib/win64"
		}
		links {
			"sdkencryptedappticket64"
		}
