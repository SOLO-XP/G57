import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const OWNER_UID = "8889b1a6-7dbd-4d42-9dcc-b231ddd0c5f2";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
};

Deno.serve(async (req) => {
  const json = (data: unknown, status = 200) =>
    new Response(JSON.stringify(data), {
      status,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    });

  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders });
  if (req.method !== "POST") return json({ error: "Method not allowed" }, 405);

  try {
    const authHeader = req.headers.get("Authorization");
    if (!authHeader?.startsWith("Bearer ")) return json({ error: "Unauthorized" }, 401);

    const supabaseUrl = Deno.env.get("SUPABASE_URL");
    const serviceRoleKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY");
    if (!supabaseUrl || !serviceRoleKey) {
      return json({ error: "Server configuration is missing required secrets" }, 500);
    }

    const adminClient = createClient(supabaseUrl, serviceRoleKey, {
      auth: { autoRefreshToken: false, persistSession: false },
    });

    const token = authHeader.slice("Bearer ".length);
    const { data: { user }, error: authError } = await adminClient.auth.getUser(token);
    if (authError || !user || user.id !== OWNER_UID) {
      return json({ error: "Forbidden: owner account only" }, 403);
    }

    const body = await req.json();
    const action = body?.action;

    if (action === "listUsers") {
      const users: Array<{ id: string; email: string | null }> = [];
      const perPage = 200;

      for (let page = 1; page <= 5; page++) {
        const { data, error } = await adminClient.auth.admin.listUsers({ page, perPage });
        if (error) return json({ error: error.message }, 400);

        users.push(...data.users.map((u) => ({
          id: u.id,
          email: u.email ?? null,
        })));

        if (data.users.length < perPage) break;
      }

      return json({ success: true, users });
    }

    if (action !== "setPassword") {
      return json({ error: "Invalid action. Use listUsers or setPassword." }, 400);
    }

    const userId = body?.userId;
    const newPassword = body?.newPassword;

    if (
      typeof userId !== "string" ||
      !/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(userId)
    ) {
      return json({ error: "Invalid user ID" }, 400);
    }

    if (typeof newPassword !== "string" || newPassword.length < 8 || newPassword.length > 128) {
      return json({ error: "Password must be between 8 and 128 characters" }, 400);
    }

    const { data, error } = await adminClient.auth.admin.updateUserById(userId, {
      password: newPassword,
    });
    if (error) return json({ error: error.message }, 400);

    return json({ success: true, message: "Password updated successfully", userId: data.user.id });
  } catch {
    return json({ error: "Invalid request or server error" }, 400);
  }
});
