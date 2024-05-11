{
  description = "An android podcast application";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
  };

  outputs = { self, nixpkgs }:
    let
      system = "x86_64-linux";
      pkgs = import nixpkgs { system = system; config.allowUnfree = true; };
    in {
      devShells.${system}.default = (import ./shell.nix { inherit pkgs; });
    };
}
